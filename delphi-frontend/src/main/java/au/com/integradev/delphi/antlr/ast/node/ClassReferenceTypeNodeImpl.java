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
package au.com.integradev.delphi.antlr.ast.node;

import au.com.integradev.delphi.antlr.ast.visitors.DelphiParserVisitor;
import au.com.integradev.delphi.type.Type;
import javax.annotation.Nonnull;
import org.antlr.runtime.Token;
import org.sonar.plugins.communitydelphi.api.ast.ClassReferenceTypeNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeReferenceNode;

public final class ClassReferenceTypeNodeImpl extends TypeNodeImpl
    implements ClassReferenceTypeNode {
  public ClassReferenceTypeNodeImpl(Token token) {
    super(token);
  }

  @Override
  public <T> T accept(DelphiParserVisitor<T> visitor, T data) {
    return visitor.visit(this, data);
  }

  @Override
  public TypeReferenceNode getClassOfTypeNode() {
    return (TypeReferenceNode) jjtGetChild(1);
  }

  @Override
  @Nonnull
  protected Type createType() {
    String image = null;
    if (parent instanceof TypeDeclarationNode) {
      image = ((TypeDeclarationNode) parent).fullyQualifiedName();
    }
    Type classType = getClassOfTypeNode().getType();
    return getTypeFactory().classOf(image, classType);
  }
}
