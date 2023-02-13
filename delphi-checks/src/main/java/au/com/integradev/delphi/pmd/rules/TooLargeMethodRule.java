/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions and Fabricio Colombo
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 * Fabricio Colombo (fabricio.colombo.mva@gmail.com)
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

import static au.com.integradev.delphi.pmd.DelphiPmdConstants.LIMIT;

import org.sonar.plugins.communitydelphi.api.ast.CompoundStatementNode;
import org.sonar.plugins.communitydelphi.api.ast.ExceptItemNode;
import org.sonar.plugins.communitydelphi.api.ast.MethodImplementationNode;
import java.util.function.Predicate;
import net.sourceforge.pmd.RuleContext;

/** Class for counting method statements. If too many, creates a violation. */
public class TooLargeMethodRule extends AbstractDelphiRule {

  @Override
  public RuleContext visit(MethodImplementationNode method, RuleContext data) {
    long statements = countStatements(method);
    int limit = getProperty(LIMIT);

    if (statements > limit) {
      addViolationWithMessage(
          data,
          method.getMethodNameNode(),
          "{0} is too large. Method has {1} statements (Limit is {2})",
          new Object[] {method.simpleName(), statements, limit});
    }

    return super.visit(method, data);
  }

  private long countStatements(MethodImplementationNode method) {
    CompoundStatementNode block = method.getStatementBlock();
    if (block != null) {
      int handlers = block.findDescendantsOfType(ExceptItemNode.class).size();
      long statements =
          block
              .descendantStatementStream()
              .filter(Predicate.not(CompoundStatementNode.class::isInstance))
              .count();

      return handlers + statements;
    }
    return 0;
  }
}
