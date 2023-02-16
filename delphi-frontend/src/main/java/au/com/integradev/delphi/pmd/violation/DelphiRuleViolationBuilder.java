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
package au.com.integradev.delphi.pmd.violation;

import au.com.integradev.delphi.pmd.FilePosition;
import au.com.integradev.delphi.pmd.rules.DelphiRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import org.sonar.plugins.communitydelphi.api.ast.DelphiNode;
import org.sonar.plugins.communitydelphi.api.ast.FileHeaderNode;
import org.sonar.plugins.communitydelphi.api.ast.MethodImplementationNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeDeclarationNode;
import org.sonar.plugins.communitydelphi.api.symbol.declaration.TypeNameDeclaration;

public class DelphiRuleViolationBuilder {
  private final DelphiRule rule;
  private final RuleContext ctx;
  private final DelphiRuleViolation ruleViolation;

  private DelphiRuleViolationBuilder(DelphiRule rule, RuleContext ctx) {
    this.rule = rule;
    this.ctx = ctx;
    this.ruleViolation = new DelphiRuleViolation(rule, ctx);
  }

  public static DelphiRuleViolationBuilder newViolation(DelphiRule rule, RuleContext ctx) {
    return new DelphiRuleViolationBuilder(rule, ctx);
  }

  public DelphiRuleViolationBuilder atPosition(FilePosition position) {
    ruleViolation.setBeginLine(position.getBeginLine());
    ruleViolation.setBeginColumn(position.getBeginColumn());
    ruleViolation.setEndLine(position.getEndLine());
    ruleViolation.setEndColumn(position.getEndColumn());
    return this;
  }

  public DelphiRuleViolationBuilder atLocation(DelphiNode node) {
    findLogicalLocation(node);
    return this;
  }

  public DelphiRuleViolationBuilder message(String message) {
    ruleViolation.setDescription(message);
    return this;
  }

  public RuleViolation build() {
    checkIfViolationSuppressed();
    return ruleViolation;
  }

  public void save() {
    ctx.getReport().addRuleViolation(build());
  }

  private void checkIfViolationSuppressed() {
    boolean suppressed = rule.isSuppressedLine(ruleViolation.getBeginLine());
    ruleViolation.setSuppressed(suppressed);
  }

  private void findLogicalLocation(DelphiNode node) {
    FileHeaderNode fileHeader = node.getAst().getFileHeader();
    ruleViolation.setPackageName(fileHeader.getName());

    TypeDeclarationNode typeNode = node.getFirstParentOfType(TypeDeclarationNode.class);
    if (typeNode != null) {
      ruleViolation.setClassName(typeNode.qualifiedNameExcludingUnit());
      ruleViolation.setClassType(typeNode.getType());
    }

    MethodImplementationNode methodNode = node.getFirstParentOfType(MethodImplementationNode.class);
    if (methodNode != null) {
      ruleViolation.setClassName(methodNode.getTypeName());
      ruleViolation.setMethodName(methodNode.simpleName());
      TypeNameDeclaration typeDeclaration = methodNode.getTypeDeclaration();
      if (typeDeclaration != null) {
        ruleViolation.setClassType(typeDeclaration.getType());
      }
    }
  }
}