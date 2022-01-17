package org.sonar.plugins.delphi.pmd.rules;

import static org.sonar.plugins.delphi.utils.conditions.RuleKey.ruleKey;
import static org.sonar.plugins.delphi.utils.conditions.RuleKeyAtLine.ruleKeyAtLine;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.delphi.utils.builders.DelphiTestUnitBuilder;

class ExtraneousArgumentListCommasRuleTest extends BasePmdRuleTest {

  @Test
  void testArgumentListWithoutTrailingCommaShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("procedure Test;")
            .appendImpl("begin")
            .appendImpl("  Foo(1, 2, 3);")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("ExtraneousArgumentListCommasRule"));
  }

  @Test
  void testArgumentListWithTrailingCommaShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("procedure Test;")
            .appendImpl("begin")
            .appendImpl("  Foo(1, 2, 3,);")
            .appendImpl("end;");

    execute(builder);

    assertIssues()
        .areExactly(1, ruleKeyAtLine("ExtraneousArgumentListCommasRule", builder.getOffset() + 3));
  }
}
