package org.sonar.plugins.delphi.pmd.rules;

import net.sourceforge.pmd.RuleContext;
import org.sonar.plugins.delphi.antlr.ast.DelphiAST;

/** Rule type which only applies to dpr and dpk files */
public abstract class DprRule extends AbstractDelphiRule {
  @Override
  public RuleContext visit(DelphiAST ast, RuleContext data) {
    if (ast.getFileName().toLowerCase().endsWith(".dpr")) {
      return super.visit(ast, data);
    }

    return data;
  }
}
