package org.sonar.plugins.delphi.antlr.ast.node;

import org.antlr.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.sonar.plugins.delphi.antlr.ast.visitors.DelphiParserVisitor;
import org.sonar.plugins.delphi.type.DelphiClassReferenceType;
import org.sonar.plugins.delphi.type.DelphiType;
import org.sonar.plugins.delphi.type.Type;
import org.sonar.plugins.delphi.type.Type.ScopedType;

public final class ClassReferenceTypeNode extends TypeNode {
  public ClassReferenceTypeNode(Token token) {
    super(token);
  }

  @Override
  public <T> T accept(DelphiParserVisitor<T> visitor, T data) {
    return visitor.visit(this, data);
  }

  private TypeReferenceNode getClassOf() {
    return (TypeReferenceNode) jjtGetChild(1);
  }

  @Override
  @NotNull
  public Type createType() {
    Type classType = getClassOf().getType();
    if (classType instanceof ScopedType) {
      return DelphiClassReferenceType.classOf((ScopedType) classType);
    }
    return DelphiType.unknownType();
  }
}