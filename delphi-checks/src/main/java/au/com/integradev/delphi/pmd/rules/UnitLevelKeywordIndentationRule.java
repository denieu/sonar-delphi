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

import au.com.integradev.delphi.antlr.DelphiLexer;
import org.sonar.plugins.communitydelphi.api.ast.DelphiAst;
import org.sonar.plugins.communitydelphi.api.ast.CompoundStatementNode;
import org.sonar.plugins.communitydelphi.api.ast.ConstSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.DelphiNode;
import org.sonar.plugins.communitydelphi.api.ast.FinalizationSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.ImplementationSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.InitializationSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.InterfaceSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.LibraryDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.PackageDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.ProgramDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeSectionNode;
import org.sonar.plugins.communitydelphi.api.ast.UnitDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.UsesClauseNode;
import org.sonar.plugins.communitydelphi.api.ast.VarSectionNode;
import au.com.integradev.delphi.utils.IndentationUtils;
import net.sourceforge.pmd.RuleContext;

public class UnitLevelKeywordIndentationRule extends AbstractDelphiRule {
  private void checkNodeIndentation(DelphiNode node, RuleContext data) {
    if (!IndentationUtils.getLineIndentation(node).equals("")) {
      addViolation(data, node.getToken());
    }
  }

  private static DelphiNode getEnd(DelphiNode node) {
    return (DelphiNode) node.getFirstChildWithId(DelphiLexer.END);
  }

  @Override
  public RuleContext visit(DelphiAst ast, RuleContext data) {
    DelphiNode end = getEnd(ast);
    if (end != null) {
      checkNodeIndentation(getEnd(ast), data);
    }
    return super.visit(ast, data);
  }

  @Override
  public RuleContext visit(UnitDeclarationNode unitDeclarationNode, RuleContext data) {
    checkNodeIndentation(unitDeclarationNode, data);
    return super.visit(unitDeclarationNode, data);
  }

  @Override
  public RuleContext visit(ProgramDeclarationNode programDeclarationNode, RuleContext data) {
    checkNodeIndentation(programDeclarationNode, data);
    return super.visit(programDeclarationNode, data);
  }

  @Override
  public RuleContext visit(LibraryDeclarationNode libraryDeclarationNode, RuleContext data) {
    checkNodeIndentation(libraryDeclarationNode, data);
    return super.visit(libraryDeclarationNode, data);
  }

  @Override
  public RuleContext visit(PackageDeclarationNode packageDeclarationNode, RuleContext data) {
    checkNodeIndentation(packageDeclarationNode, data);
    return super.visit(packageDeclarationNode, data);
  }

  @Override
  public RuleContext visit(UsesClauseNode usesClauseNode, RuleContext data) {
    checkNodeIndentation(usesClauseNode, data);
    return super.visit(usesClauseNode, data);
  }

  @Override
  public RuleContext visit(InterfaceSectionNode interfaceSectionNode, RuleContext data) {
    checkNodeIndentation(interfaceSectionNode, data);
    return super.visit(interfaceSectionNode, data);
  }

  @Override
  public RuleContext visit(ImplementationSectionNode implementationSectionNode, RuleContext data) {
    checkNodeIndentation(implementationSectionNode, data);
    return super.visit(implementationSectionNode, data);
  }

  @Override
  public RuleContext visit(TypeSectionNode typeSectionNode, RuleContext data) {
    if (typeSectionNode.jjtGetParent() instanceof InterfaceSectionNode
        || typeSectionNode.jjtGetParent() instanceof ImplementationSectionNode) {
      checkNodeIndentation(typeSectionNode, data);
    }
    return super.visit(typeSectionNode, data);
  }

  @Override
  public RuleContext visit(VarSectionNode varSectionNode, RuleContext data) {
    if (varSectionNode.jjtGetParent() instanceof InterfaceSectionNode
        || varSectionNode.jjtGetParent() instanceof ImplementationSectionNode) {
      checkNodeIndentation(varSectionNode, data);
    }
    return super.visit(varSectionNode, data);
  }

  @Override
  public RuleContext visit(ConstSectionNode constSectionNode, RuleContext data) {
    if (constSectionNode.jjtGetParent() instanceof InterfaceSectionNode
        || constSectionNode.jjtGetParent() instanceof ImplementationSectionNode) {
      checkNodeIndentation(constSectionNode, data);
    }
    return super.visit(constSectionNode, data);
  }

  @Override
  public RuleContext visit(CompoundStatementNode compoundStatementNode, RuleContext data) {
    if (compoundStatementNode.jjtGetParent() instanceof DelphiAst) {
      checkNodeIndentation(compoundStatementNode, data);
      checkNodeIndentation(getEnd(compoundStatementNode), data);
    }
    return super.visit(compoundStatementNode, data);
  }

  @Override
  public RuleContext visit(InitializationSectionNode initializationSectionNode, RuleContext data) {
    checkNodeIndentation(initializationSectionNode, data);
    return super.visit(initializationSectionNode, data);
  }

  @Override
  public RuleContext visit(FinalizationSectionNode finalizationSectionNode, RuleContext data) {
    checkNodeIndentation(finalizationSectionNode, data);
    return super.visit(finalizationSectionNode, data);
  }
}