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

import org.sonar.check.Rule;
import org.sonar.plugins.communitydelphi.api.ast.ExceptItemNode;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheck;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheckContext;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@DeprecatedRuleKey(ruleKey = "CatchingGeneralExceptionRule", repositoryKey = "delph")
@Rule(key = "CatchingRawException")
public class CatchingRawExceptionCheck extends DelphiCheck {
  private static final String MESSAGE = "Catch a more specific exception type.";

  @Override
  public DelphiCheckContext visit(ExceptItemNode exceptItem, DelphiCheckContext context) {
    if (exceptItem.getExceptionType().getImage().equalsIgnoreCase("Exception")) {
      reportIssue(context, exceptItem, MESSAGE);
    }
    return super.visit(exceptItem, context);
  }
}
