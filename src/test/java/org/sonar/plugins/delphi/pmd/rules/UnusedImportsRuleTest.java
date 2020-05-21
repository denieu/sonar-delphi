package org.sonar.plugins.delphi.pmd.rules;

import static org.sonar.plugins.delphi.utils.conditions.RuleKey.ruleKey;
import static org.sonar.plugins.delphi.utils.conditions.RuleKeyAtLine.ruleKeyAtLine;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.delphi.pmd.xml.DelphiRule;
import org.sonar.plugins.delphi.pmd.xml.DelphiRuleProperty;
import org.sonar.plugins.delphi.utils.builders.DelphiTestUnitBuilder;

public class UnusedImportsRuleTest extends BasePmdRuleTest {
  private final DelphiRuleProperty property = new DelphiRuleProperty("exclusions");

  @Before
  public void setup() {
    DelphiRule rule = new DelphiRule();
    rule.setClazz("org.sonar.plugins.delphi.pmd.rules.UnusedImportsRule");
    rule.setPriority(4);
    rule.setName("UnusedImportsRule_TEST");
    rule.setProperties(List.of(property));
    addRule(rule);
  }

  @Test
  public void testUnusedImportShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("  System.SysUtils;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areExactly(1, ruleKeyAtLine("UnusedImportsRule_TEST", builder.getOffset() + 2));
  }

  @Test
  public void testUnresolvedImportShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("  NONEXISTENT_UNIT;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testImplicitlyUsedImportShouldNotIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("  System.SysUtils;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("  FreeAndNil(Obj);")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testExplicitlyUsedImportShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("  System.SysUtils;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("  System.SysUtils.FreeAndNil(Obj);")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testHelperMethodUsedImportShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .unitName("Foos")
            .appendDecl("function Bar: Boolean;")
            .appendImpl("uses")
            .appendImpl("  System.SysUtils;")
            .appendImpl("function Foo: Boolean;")
            .appendImpl("begin")
            .appendImpl("  Result := Foos.Bar.NONEXISTENT;")
            .appendImpl("end;")
            .appendImpl("function Bar: Boolean;")
            .appendImpl("begin")
            .appendImpl("  Result := ''.IsEmpty;")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testComponentAncestorImportShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendDecl("uses")
            .appendDecl("    Vcl.Controls")
            .appendDecl("  , System.Classes")
            .appendDecl("  ;")
            .appendDecl("type")
            .appendDecl("  TFooControl = class(TCustomControl)")
            .appendDecl("  end;")
            .appendDecl("  TFooComponent = class(TCustomControl)")
            .appendDecl("    FControl: TFooControl;")
            .appendDecl("  end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testComponentAncestorImportWithPublishedFieldInNonComponentTypeShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendDecl("uses")
            .appendDecl("    Vcl.Controls")
            .appendDecl("  , System.Classes")
            .appendDecl("  ;")
            .appendDecl("type")
            .appendDecl("  TFooControl = class(TCustomControl)")
            .appendDecl("  end;")
            .appendDecl("  TFoo = class(TObject)")
            .appendDecl("    FControl: TFooControl;")
            .appendDecl("  end;");

    execute(builder);

    assertIssues()
        .areExactly(1, ruleKeyAtLine("UnusedImportsRule_TEST", builder.getOffsetDecl() + 3));
  }

  @Test
  public void testComponentAncestorImportWithNonPublishedFieldShouldAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendDecl("uses")
            .appendDecl("    Vcl.Controls")
            .appendDecl("  , System.Classes")
            .appendDecl("  ;")
            .appendDecl("type")
            .appendDecl("  TFooControl = class(TCustomControl)")
            .appendDecl("  end;")
            .appendDecl("  TFooComponent = class(TCustomControl)")
            .appendDecl("  public")
            .appendDecl("    FControl: TFooControl;")
            .appendDecl("  end;");

    execute(builder);

    assertIssues()
        .areExactly(1, ruleKeyAtLine("UnusedImportsRule_TEST", builder.getOffsetDecl() + 3));
  }

  @Test
  public void testExcludedUnusedImportInInterfaceSectionShouldNotAddIssue() {
    property.setValue("System.SysUtils");

    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendDecl("uses")
            .appendDecl("  System.SysUtils;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }

  @Test
  public void testExcludedUnusedImportInImplementationSectionShouldAddIssue() {
    property.setValue("System.SysUtils");

    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("  System.SysUtils;")
            .appendImpl("procedure Test;")
            .appendImpl("var")
            .appendImpl("  Obj: TObject;")
            .appendImpl("begin")
            .appendImpl("  Obj := TObject.Create;")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areExactly(1, ruleKeyAtLine("UnusedImportsRule_TEST", builder.getOffset() + 2));
  }

  @Test
  public void testImportsRequiredByInlineMethodsShouldNotAddIssue() {
    DelphiTestUnitBuilder builder =
        new DelphiTestUnitBuilder()
            .appendImpl("uses")
            .appendImpl("    System.UITypes")
            .appendImpl("  , Vcl.Dialogs")
            .appendImpl("  ;")
            .appendImpl("procedure Test;")
            .appendImpl("begin")
            .appendImpl("  MessageDlg('Spooky error!', mtError, [mbOK], 0);")
            .appendImpl("end;");

    execute(builder);

    assertIssues().areNot(ruleKey("UnusedImportsRule_TEST"));
  }
}