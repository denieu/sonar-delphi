/*
 * Sonar Delphi Plugin
 * Copyright (C) 2019-2022 Integrated Application Development
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package au.com.integradev.delphi.pmd.rules;

import org.sonar.plugins.communitydelphi.api.symbol.declaration.MethodNameDeclaration;
import org.sonar.plugins.communitydelphi.api.symbol.declaration.TypeNameDeclaration;
import org.sonar.plugins.communitydelphi.api.ast.ArgumentListNode;
import org.sonar.plugins.communitydelphi.api.ast.AssignmentStatementNode;
import org.sonar.plugins.communitydelphi.api.ast.BinaryExpressionNode;
import org.sonar.plugins.communitydelphi.api.ast.DelphiNode;
import org.sonar.plugins.communitydelphi.api.ast.ExpressionNode;
import org.sonar.plugins.communitydelphi.api.ast.IdentifierNode;
import org.sonar.plugins.communitydelphi.api.ast.NameReferenceNode;
import org.sonar.plugins.communitydelphi.api.ast.PrimaryExpressionNode;
import org.sonar.plugins.communitydelphi.api.ast.RaiseStatementNode;
import au.com.integradev.delphi.operator.BinaryOperator;
import org.sonar.plugins.communitydelphi.api.symbol.declaration.NameDeclaration;
import org.sonar.plugins.communitydelphi.api.symbol.declaration.MethodKind;
import org.sonar.plugins.communitydelphi.api.type.Type;
import org.sonar.plugins.communitydelphi.api.type.Type.ProceduralType;
import org.sonar.plugins.communitydelphi.api.type.Typed;
import org.sonar.plugins.communitydelphi.api.type.Parameter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import org.sonar.plugins.communitydelphi.api.symbol.declaration.VariableNameDeclaration;

public class MemoryManagementRule extends AbstractDelphiRule {
  @VisibleForTesting
  static final PropertyDescriptor<List<String>> MEMORY_FUNCTIONS =
      PropertyFactory.stringListProperty("memoryFunctions")
          .desc("A list of functions used for memory management")
          .emptyDefaultValue()
          .build();

  @VisibleForTesting
  static final PropertyDescriptor<List<String>> WHITELISTED_NAMES =
      PropertyFactory.stringListProperty("whitelist")
          .desc("A list of constructor names which don't require memory management.")
          .emptyDefaultValue()
          .build();

  private Set<String> memoryFunctions;
  private Set<String> whitelist;

  public MemoryManagementRule() {
    definePropertyDescriptor(MEMORY_FUNCTIONS);
    definePropertyDescriptor(WHITELISTED_NAMES);
  }

  @Override
  public void start(RuleContext data) {
    memoryFunctions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    memoryFunctions.addAll(getProperty(MEMORY_FUNCTIONS));

    whitelist = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    whitelist.addAll(getProperty(WHITELISTED_NAMES));
  }

  @Override
  public RuleContext visit(PrimaryExpressionNode expression, RuleContext data) {
    if (shouldVisit(expression)) {
      expression.findChildrenOfType(NameReferenceNode.class).stream()
          .flatMap(reference -> reference.flatten().stream())
          .filter(MemoryManagementRule::requiresMemoryManagement)
          .map(NameReferenceNode::getIdentifier)
          .filter(identifier -> !whitelist.contains(identifier.getImage()))
          .forEach(violationNode -> addViolation(data, violationNode));
    }

    return super.visit(expression, data);
  }

  private void addViolation(RuleContext data, IdentifierNode violationNode) {}

  private boolean shouldVisit(PrimaryExpressionNode expression) {
    if (expression.isInheritedCall()) {
      return false;
    }

    if (isInterfaceVariableAssignment(expression)) {
      return false;
    }

    if (isInterfaceParameter(expression)) {
      return false;
    }

    if (isExceptionRaise(expression)) {
      return false;
    }

    return !isMemoryManaged(expression);
  }

  private static boolean isInterfaceVariableAssignment(PrimaryExpressionNode expression) {
    DelphiNode assignStatement = findParentSkipCasts(expression);
    if (assignStatement instanceof AssignmentStatementNode) {
      Type assignedType = ((AssignmentStatementNode) assignStatement).getAssignee().getType();
      return assignedType.isInterface();
    }
    return false;
  }

  private static boolean isInterfaceParameter(ExpressionNode expression) {
    DelphiNode parent = findParentSkipCasts(expression);

    if (!(parent instanceof ArgumentListNode)) {
      return false;
    }

    DelphiNode previous = parent.jjtGetParent().jjtGetChild(parent.jjtGetChildIndex() - 1);
    if (!(previous instanceof Typed)) {
      return false;
    }

    Type type = ((Typed) previous).getType();
    if (!type.isProcedural()) {
      return false;
    }

    DelphiNode argument = expression;
    while (argument.jjtGetParent() != parent) {
      argument = argument.jjtGetParent();
    }

    List<ExpressionNode> arguments = ((ArgumentListNode) parent).getArguments();
    int argumentIndex = Iterables.indexOf(arguments, argument::equals);

    Parameter parameter = ((ProceduralType) type).getParameter(argumentIndex);
    return parameter.getType().isInterface();
  }

  private static boolean isExceptionRaise(PrimaryExpressionNode expression) {
    return findParentSkipCasts(expression) instanceof RaiseStatementNode;
  }

  private boolean isMemoryManaged(PrimaryExpressionNode expression) {
    DelphiNode parent = findParentSkipCasts(expression);

    if (!(parent instanceof ArgumentListNode)) {
      return false;
    }

    DelphiNode node = parent.jjtGetParent().jjtGetChild(parent.jjtGetChildIndex() - 1);
    if (!(node instanceof NameReferenceNode)) {
      return false;
    }

    NameReferenceNode nameReference = ((NameReferenceNode) node).getLastName();
    NameDeclaration declaration = nameReference.getNameDeclaration();
    if (declaration instanceof MethodNameDeclaration) {
      var method = (MethodNameDeclaration) declaration;
      return memoryFunctions.contains(method.fullyQualifiedName());
    }

    return false;
  }

  private static DelphiNode findParentSkipCasts(ExpressionNode expression) {
    DelphiNode result = getParentSkipParentheses(expression);
    while (true) {
      if (isSoftCast(result)) {
        result = getParentSkipParentheses(result);
      } else if (isHardCast(result)) {
        result = getNthParentSkipParentheses(result, 2);
      } else {
        return result;
      }
    }
  }

  private static DelphiNode getParentSkipParentheses(DelphiNode node) {
    return getNthParentSkipParentheses(node, 1);
  }

  private static DelphiNode getNthParentSkipParentheses(DelphiNode node, int n) {
    for (int i = 0; i < n; ++i) {
      if (node instanceof ExpressionNode) {
        node = ((ExpressionNode) node).findParentheses();
      }
      node = node.jjtGetParent();
    }
    return node;
  }

  private static boolean isSoftCast(DelphiNode node) {
    return node instanceof BinaryExpressionNode
        && ((BinaryExpressionNode) node).getOperator() == BinaryOperator.AS;
  }

  private static boolean isHardCast(DelphiNode node) {
    if (node instanceof ArgumentListNode) {
      ArgumentListNode argumentList = (ArgumentListNode) node;
      DelphiNode previous =
          argumentList.jjtGetParent().jjtGetChild(argumentList.jjtGetChildIndex() - 1);
      if (previous instanceof NameReferenceNode && isLastChild(argumentList)) {
        NameReferenceNode nameReference = ((NameReferenceNode) previous);
        NameDeclaration declaration = nameReference.getLastName().getNameDeclaration();
        return declaration instanceof TypeNameDeclaration;
      }
    }
    return false;
  }

  private static boolean isLastChild(DelphiNode node) {
    return node.jjtGetChildIndex() == node.jjtGetParent().jjtGetNumChildren() - 1;
  }

  private static boolean requiresMemoryManagement(NameReferenceNode reference) {
    NameDeclaration declaration = reference.getNameDeclaration();
    if (declaration instanceof MethodNameDeclaration) {
      MethodNameDeclaration method = (MethodNameDeclaration) declaration;
      MethodKind kind = method.getMethodKind();

      if (kind == MethodKind.CONSTRUCTOR) {
        NameReferenceNode previous = reference.prevName();
        return previous != null
            && !isExplicitSelf(previous)
            && !isObjectInstance(previous)
            && !isRecordConstructor(method);
      }

      if (kind == MethodKind.FUNCTION) {
        return method.getName().equalsIgnoreCase("Clone") && returnsCovariantType(method);
      }
    }
    return false;
  }

  private static boolean isExplicitSelf(NameReferenceNode reference) {
    return reference.getNameOccurrence() != null && reference.getNameOccurrence().isSelf();
  }

  private static boolean isObjectInstance(NameReferenceNode reference) {
    NameDeclaration declaration = reference.getNameDeclaration();
    return declaration instanceof VariableNameDeclaration
        && !((Typed) declaration).getType().isClassReference();
  }

  private static boolean isRecordConstructor(MethodNameDeclaration method) {
    TypeNameDeclaration typeDeclaration = method.getTypeDeclaration();
    return typeDeclaration != null && typeDeclaration.getType().isRecord();
  }

  private static boolean returnsCovariantType(MethodNameDeclaration method) {
    TypeNameDeclaration typeDeclaration = method.getTypeDeclaration();
    if (typeDeclaration != null) {
      Type methodType = typeDeclaration.getType();
      Type returnType = method.getReturnType();

      return methodType.is(returnType) || methodType.isSubTypeOf(returnType);
    }
    return false;
  }
}