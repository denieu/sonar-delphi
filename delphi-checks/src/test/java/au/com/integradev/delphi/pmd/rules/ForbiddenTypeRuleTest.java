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

import static au.com.integradev.delphi.utils.conditions.RuleKey.ruleKey;
import static au.com.integradev.delphi.utils.conditions.RuleKeyAtLine.ruleKeyAtLine;

import au.com.integradev.delphi.pmd.xml.DelphiRule;
import au.com.integradev.delphi.pmd.xml.DelphiRuleProperty;
import au.com.integradev.delphi.utils.builders.DelphiTestUnitBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForbiddenTypeRuleTest extends BasePmdRuleTest {
  private static final String UNIT_NAME = "TestUnit";
  private static final String FORBIDDEN_TYPES = "TestUnit.TFoo|TestUnit.TFoo.TBar";

  @BeforeEach
  void setup() {
    DelphiRule rule = new DelphiRule();
    DelphiRuleProperty blacklist =
        new DelphiRuleProperty(ForbiddenTypeRule.BLACKLISTED_TYPES.name(), FORBIDDEN_TYPES);

    rule.setName("ForbiddenTypeRuleTest");
    rule.setTemplateName("ForbiddenTypeRule");
    rule.setPriority(5);
    rule.addProperty(blacklist);
    rule.setClazz("au.com.integradev.delphi.pmd.rules.ForbiddenTypeRule");

    addRule(rule);
  }

  @Test
  void testForbiddenTypeUsageShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .unitName(UNIT_NAME)
            .appendDecl("type")
            .appendDecl("  TFoo = class(TObject)")
            .appendDecl("    class procedure Bar;")
            .appendDecl("  end;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Foo: TFoo;")
            .appendImpl("begin")
            .appendImpl("  TFoo.Bar;")
            .appendImpl("end;");

    execute(builder);

    assertIssues()
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 3))
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 5));
  }

  @Test
  void testForbiddenNestedTypeUsageShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .unitName(UNIT_NAME)
            .appendDecl("type")
            .appendDecl("  TFoo = class(TObject)")
            .appendDecl("  type")
            .appendDecl("    TNested = class(TObject)")
            .appendDecl("      class procedure Bar;")
            .appendDecl("    end;")
            .appendDecl("    class procedure Bar;")
            .appendDecl("  end;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Foo: TFoo;")
            .appendImpl("  Nested: TFoo.TNested;")
            .appendImpl("begin")
            .appendImpl("  TFoo.Bar;")
            .appendImpl("  TFoo.TNested.Bar;")
            .appendImpl("end;");

    execute(builder);

    assertIssues()
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 3))
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 4))
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 6))
        .areExactly(1, ruleKeyAtLine("ForbiddenTypeRuleTest", builder.getOffset() + 7));
  }

  @Test
  void testMethodImplementationShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .unitName(UNIT_NAME)
            .appendDecl("type")
            .appendDecl("  TFoo = class(TObject)")
            .appendDecl("    procedure Bar; virtual;")
            .appendDecl("  end;")
            .appendImpl("procedure TFoo.Bar;")
            .appendImpl("begin")
            .appendImpl("  // Do nothing")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("ForbiddenTypeRuleTest"));
  }
}