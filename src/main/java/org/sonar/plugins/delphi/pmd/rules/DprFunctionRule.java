/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
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
package org.sonar.plugins.delphi.pmd.rules;

import org.sonar.plugins.delphi.antlr.DelphiLexer;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;

/**
 * Rule class searching for procedures, functions and variables in a .dpr file
 */
public class DprFunctionRule extends DelphiRule {

  private int check; // check for .dpr

  @Override
  public void init() {
    check = -1; // needs to check at new file
  }

  @Override
  public Object visit(DelphiPMDNode node, Object data) {
    if (check == -1) { // checking if we are on .dpr/.dpk
      if (node.getASTTree().getFileName().endsWith(".dpr") || node.getASTTree().getFileName().endsWith(".dpk")) {
        check = 1;
      } else {
        check = 0;
      }
    }
    if (check != 1) {
      return data; // not a .dpr/.dpk file
    }

    if (isViolationNode(node)) {
      addViolation(data, node);
    }

    return data;
  }

  /**
   * Check if node is a procedure/function node, or a variable node
   * 
   * @param node
   *          Node to check
   * @return True if so, false otherwise
   */
  protected boolean isViolationNode(DelphiPMDNode node) {
    int type = node.getType();
    return type == DelphiLexer.PROCEDURE || type == DelphiLexer.FUNCTION;
  }

}