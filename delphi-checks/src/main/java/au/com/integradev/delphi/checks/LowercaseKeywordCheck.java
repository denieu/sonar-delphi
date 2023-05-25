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
package au.com.integradev.delphi.checks;

import au.com.integradev.delphi.core.DelphiKeywords;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.communitydelphi.api.ast.AsmStatementNode;
import org.sonar.plugins.communitydelphi.api.ast.DelphiNode;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheck;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheckContext;
import org.sonar.plugins.communitydelphi.api.check.FilePosition;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@DeprecatedRuleKey(ruleKey = "LowerCaseReservedWordsRule", repositoryKey = "delph")
@Rule(key = "LowercaseKeyword")
public class LowercaseKeywordCheck extends DelphiCheck {
  private static final String MESSAGE = "Lowercase this keyword.";

  @RuleProperty(
      key = "prefixes",
      description = "Comma-delimited list of keywords that this rule ignores (case-insensitive).")
  private String excluded = "";

  private Set<String> excludedSet;

  @Override
  public void start(DelphiCheckContext context) {
    excludedSet =
        ImmutableSortedSet.copyOf(
            String.CASE_INSENSITIVE_ORDER, Splitter.on(',').trimResults().split(excluded));
  }

  @Override
  public DelphiCheckContext visit(DelphiNode node, DelphiCheckContext context) {
    if (isViolationNode(node)) {
      context
          .newIssue()
          .onFilePosition(FilePosition.from(node.getToken()))
          .withMessage(MESSAGE)
          .report();
    }
    return super.visit(node, context);
  }

  @Override
  public DelphiCheckContext visit(AsmStatementNode node, DelphiCheckContext context) {
    // Do not look inside asm blocks
    return context;
  }

  private boolean isViolationNode(DelphiNode node) {
    if (!DelphiKeywords.KEYWORDS.contains(node.jjtGetId())) {
      return false;
    }

    var image = node.getToken().getImage();
    return !excludedSet.contains(image) && !StringUtils.isAllLowerCase(image);
  }
}
