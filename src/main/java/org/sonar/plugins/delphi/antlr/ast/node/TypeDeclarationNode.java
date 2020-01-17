package org.sonar.plugins.delphi.antlr.ast.node;

import java.util.ArrayDeque;
import java.util.Deque;
import org.antlr.runtime.Token;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.plugins.delphi.antlr.ast.visitors.DelphiParserVisitor;
import org.sonar.plugins.delphi.symbol.Qualifiable;
import org.sonar.plugins.delphi.symbol.QualifiedName;
import org.sonar.plugins.delphi.symbol.declaration.TypeNameDeclaration;
import org.sonar.plugins.delphi.type.Type;
import org.sonar.plugins.delphi.type.Typed;

public final class TypeDeclarationNode extends DelphiNode implements Typed, Qualifiable {
  private Boolean isSubType;
  private QualifiedName qualifiedName;
  private String qualifiedNameExcludingUnit;

  public TypeDeclarationNode(Token token) {
    super(token);
  }

  public TypeDeclarationNode(int tokenType) {
    super(tokenType);
  }

  @Override
  public <T> T accept(DelphiParserVisitor<T> visitor, T data) {
    return visitor.visit(this, data);
  }

  @Override
  public String getImage() {
    return simpleName();
  }

  public QualifiedNameDeclarationNode getTypeNameNode() {
    return (QualifiedNameDeclarationNode) jjtGetChild(0);
  }

  public TypeNode getTypeNode() {
    return (TypeNode) jjtGetChild(1);
  }

  @Nullable
  public TypeNameDeclaration getTypeNameDeclaration() {
    return (TypeNameDeclaration) getTypeNameNode().getNameDeclaration();
  }

  @Override
  public String simpleName() {
    return getTypeNameNode().simpleName();
  }

  @Override
  public QualifiedName getQualifiedName() {
    if (qualifiedName == null) {
      buildQualifiedNames();
    }

    return qualifiedName;
  }

  public String qualifiedNameExcludingUnit() {
    if (qualifiedNameExcludingUnit == null) {
      buildQualifiedNames();
    }
    return qualifiedNameExcludingUnit;
  }

  private void buildQualifiedNames() {
    TypeDeclarationNode node = this;
    Deque<String> names = new ArrayDeque<>();

    while (node != null) {
      names.addFirst(node.getTypeNameNode().simpleName());
      node = node.getFirstParentOfType(TypeDeclarationNode.class);
    }

    this.qualifiedNameExcludingUnit = StringUtils.join(names, ".");
    names.addFirst(findUnitName());

    qualifiedName = new QualifiedName(names);
  }

  public boolean isClass() {
    return getTypeNode() instanceof ClassTypeNode;
  }

  public boolean isClassHelper() {
    return getTypeNode() instanceof ClassHelperTypeNode;
  }

  public boolean isEnum() {
    return getTypeNode() instanceof EnumTypeNode;
  }

  public boolean isInterface() {
    return getTypeNode() instanceof InterfaceTypeNode;
  }

  public boolean isObject() {
    return getTypeNode() instanceof ObjectTypeNode;
  }

  public boolean isRecord() {
    return getTypeNode() instanceof RecordTypeNode;
  }

  public boolean isRecordHelper() {
    return getTypeNode() instanceof RecordHelperTypeNode;
  }

  public boolean isPointer() {
    return getTypeNode() instanceof PointerTypeNode;
  }

  public boolean isNestedType() {
    if (isSubType == null) {
      isSubType = getFirstParentOfType(TypeDeclarationNode.class) != null;
    }
    return isSubType;
  }

  public boolean isTypeAlias() {
    return getTypeNode() instanceof TypeAliasNode;
  }

  public boolean isTypeType() {
    return getTypeNode() instanceof TypeTypeNode;
  }

  public boolean isForwardDeclaration() {
    TypeNameDeclaration declaration = getTypeNameDeclaration();
    return declaration != null && declaration.isForwardDeclaration();
  }

  @Override
  @NotNull
  public Type getType() {
    return getTypeNode().getType();
  }
}