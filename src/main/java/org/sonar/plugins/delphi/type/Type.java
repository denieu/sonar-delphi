package org.sonar.plugins.delphi.type;

import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.plugins.delphi.symbol.scope.DelphiScope;

public interface Type {

  /**
   * Image which describes this type
   *
   * @return The image that describes this type
   */
  String getImage();

  /**
   * Returns the concrete type that this inherits from. Note that this will never return an
   * interface.
   *
   * @return The type this inherits from. UnknownType if this does not inherit from a concrete type.
   */
  Type superType();

  /**
   * Returns all types from the ancestor list. (This will also return interfaces.)
   *
   * @return The types that this inherits from.
   */
  Set<Type> parents();

  /**
   * Check whether a type is the one designated by the qualified name.
   *
   * @param image Type image to check
   * @return true if the type is the one looked for
   */
  boolean is(String image);

  /**
   * Check whether a type is equivalent to another
   *
   * @param type Type to compare against
   * @return true if the types are equivalent
   */
  boolean is(Type type);

  /**
   * Check whether a type is a subtype of another.
   *
   * @param image Type image of a potential superType
   * @return true if types are equivalent or if the one passed in parameter is in the hierarchy.
   */
  boolean isSubTypeOf(String image);

  /**
   * Check whether a type is a subtype of another.
   *
   * @param superType instance of a potential superType.
   * @return true if types are equivalent or if the one passed in parameter is in the hierarchy.
   */
  boolean isSubTypeOf(Type superType);

  /**
   * Check if this type is untyped.
   *
   * @see <a href="http://pages.cs.wisc.edu/~rkennedy/untyped">What is an untyped parameter?</a>
   * @return true if type is untyped
   */
  boolean isUntyped();

  /**
   * Check if this type is unresolved. This happens when the type declaration cannot be found, but
   * we have the image.
   *
   * @return true if type has not been resolved
   */
  boolean isUnresolved();

  /**
   * Check if this type is unknown. This happens when we can't find any information for a type, not
   * even its image.
   *
   * @return true if type is unknown
   */
  boolean isUnknown();

  /**
   * Check if this type is void. An example of this would be the "return type" of a procedure.
   *
   * @return true if the type is void
   */
  boolean isVoid();

  /**
   * Check if this type is an interface type
   *
   * @return true if the type is an interface type
   */
  boolean isInterface();

  /**
   * Check if this type is a record type
   *
   * @return true if the type is a record type
   */
  boolean isRecord();

  /**
   * Check if this type is an enumeration type
   *
   * @return true if the type is an enumeration type
   */
  boolean isEnum();

  /**
   * Check if this type is an integer type
   *
   * @return true if the type is an integer type
   */
  boolean isInteger();

  /**
   * Check if this type is a decimal type
   *
   * @return true if the type is a decimal type
   */
  boolean isDecimal();

  /**
   * Check if this type is a text type (Char, ShortString, String, etc...)
   *
   * @return true if the type is a text type
   */
  boolean isText();

  /**
   * Check if this type is a string type (ShortString, String, etc...)
   *
   * @return true if the type is a string type
   */
  boolean isString();

  /**
   * Check if this type is a narrow string type (AnsiString, etc...)
   *
   * @return true if the type is a narrow string type
   */
  boolean isNarrowString();

  /**
   * Check if this type is a wide string type (String, WideString, UnicodeString, etc...)
   *
   * @return true if the type is a wide string type
   */
  boolean isWideString();

  /**
   * Check if this type is a char type (Char, WideChar, etc...)
   *
   * @return true if the type is a char type
   */
  boolean isChar();

  /**
   * Check if this type is a narrow char type (AnsiChar)
   *
   * @return true if the type is a narrow char type
   */
  boolean isNarrowChar();

  /**
   * Check if this type is a wide char type (Char or WideChar)
   *
   * @return true if the type is a wide char type
   */
  boolean isWideChar();

  /**
   * Check if this type is a boolean type (Boolean, ByteBool, WordBool, etc...)
   *
   * @return true if the type is a boolean type
   */
  boolean isBoolean();

  /**
   * Check if this type is a struct type (object, class, record, etc...)
   *
   * @return true if the type is a struct type
   */
  boolean isStruct();

  /**
   * Check if this type is a file type
   *
   * @return true if the type is a file type
   */
  boolean isFile();

  /**
   * Check if this type is an array
   *
   * @return true if the type is an array
   */
  boolean isArray();

  /**
   * Check if this type is a fixed-size array
   *
   * @return true if the type is a fixed array
   */
  boolean isFixedArray();

  /**
   * Check if this type is a dynamic array
   *
   * @return true if the type is a dynamic array
   */
  boolean isDynamicArray();

  /**
   * Check if this type is an open array
   *
   * @return true if the type is an open array
   */
  boolean isOpenArray();

  /**
   * Check if this type is an array of const
   *
   * @return true if the type is an array of const
   */
  boolean isArrayOfConst();

  /**
   * Check if this type is a pointer
   *
   * @return true if the type is a pointer
   */
  boolean isPointer();

  /**
   * Check if this type is a set
   *
   * @return true if the type is a set
   */
  boolean isSet();

  /**
   * Check if this type is a procedural type
   *
   * @return true if the type is a procedural type
   */
  boolean isProcedural();

  /**
   * Check if this type is a method type
   *
   * @return true if the type is a method type
   */
  boolean isMethod();

  /**
   * Check if this type is a class reference
   *
   * @return true if the type is a class reference
   */
  boolean isClassReference();

  /**
   * Check if this type is a variant
   *
   * @return true if the type is a variant
   */
  boolean isVariant();

  /**
   * Check if this type is a 'type type'
   *
   * @return true if the type is a 'type type'
   */
  boolean isTypeType();

  /**
   * Check if this type is an array constructor
   *
   * @return true if the type is an array constructor
   */
  boolean isArrayConstructor();

  interface CollectionType extends Type {
    /**
     * The type that is is a collection of
     *
     * @return Element type
     */
    @NotNull
    Type elementType();
  }

  interface ArrayConstructorType extends Type {
    /**
     * The types of the elements passed in to this array constructor
     *
     * @return Element types
     */
    List<Type> elementTypes();

    /**
     * Returns whether the array constructor is empty
     *
     * @return true if the array constructor has no elements
     */
    default boolean isEmpty() {
      return elementTypes().isEmpty();
    }
  }

  interface ScopedType extends Type {
    /**
     * The scope of this type's implementation.
     *
     * @return Type scope
     */
    @NotNull
    DelphiScope typeScope();
  }

  interface StructType extends ScopedType {
    /**
     * The kind of struct that this type is
     *
     * @return Struct kind
     */
    StructKind kind();

    /**
     * Whether this is a forward type
     *
     * @return true if this is a forward type
     */
    boolean isForwardType();

    /**
     * Adds the full type declaration's information. Also marks this StructType instance as a
     * forward type.
     *
     * @param fullType Type representing the full type declaration
     */
    void setFullType(StructType fullType);

    /**
     * Returns a set of all default array properties that can be called on this type.
     *
     * @return Set of default array property declarations
     */
    Set<NameDeclaration> findDefaultArrayProperties();
  }

  interface HelperType extends StructType {
    /**
     * The type that this is a helper for.
     *
     * @return Helper type
     */
    @NotNull
    Type helperType();
  }

  interface PointerType extends Type {
    /**
     * The type which this type dereferences to
     *
     * @return Dereferenced type
     */
    @NotNull
    Type dereferencedType();

    /**
     * Check if this pointer is a nil literal
     *
     * @return true if this pointer is a nil literal
     */
    boolean isNilPointer();

    /**
     * Check if this pointer is untyped
     *
     * @return true if this pointer is untyped
     */
    boolean isUntypedPointer();
  }

  interface ProceduralType extends Type {
    /**
     * NOTE: The order of this enum matters. The ordinal value is used to determine which kind gets
     * preference during overload resolution.
     *
     * @see org.sonar.plugins.delphi.symbol.resolve.InvocationResolver
     */
    enum ProceduralKind {
      PROCEDURE,
      PROCEDURE_OF_OBJECT,
      REFERENCE,
      ANONYMOUS,
      METHOD
    }

    /**
     * The type that this method returns
     *
     * @return Return type
     */
    Type returnType();

    /**
     * The types of the parameters that this method expects
     *
     * @return Expected types of parameters
     */
    List<Type> parameterTypes();

    /**
     * The kind of procedural type that this type is
     *
     * @return Procedural kind
     */
    ProceduralKind kind();
  }

  interface FileType extends Type {
    /**
     * The type that this file is comprised of
     *
     * @return File type
     */
    Type fileType();
  }

  interface EnumType extends ScopedType {
    /**
     * The base type that this is an enumeration of
     *
     * @return Base type
     */
    @Nullable
    Type baseType();
  }

  interface ClassReferenceType extends Type {
    /**
     * The class type that this references
     *
     * @return Class type
     */
    ScopedType classType();
  }

  interface TypeType extends Type {
    /**
     * The type that this type is based off of
     *
     * @return Original type
     */
    Type originalType();
  }

  /**
   * Most Type objects are immutable in the sense that application code cannot modify them once they
   * are created.
   *
   * <p>This interface is used to tag Type objects which are provably and deeply immutable. <br>
   * This is helpful if you want to store Type objects in enums, which should only contain immutable
   * members.
   */
  @Immutable
  interface ImmutableType extends Type {}

  @Immutable
  interface ImmutableFileType extends FileType, ImmutableType {}

  @Immutable
  interface ImmutablePointerType extends PointerType, ImmutableType {}

  @Immutable
  interface IntegerType extends ImmutableType {
    /**
     * The size of this integer type
     *
     * @return Size
     */
    int size();

    /**
     * Minimum value that this type can hold
     *
     * @return minimum value
     */
    BigInteger min();

    /**
     * Maximum value that this type can hold
     *
     * @return maximum value
     */
    BigInteger max();

    /**
     * Returns whether the type is signed
     *
     * @return true if the type is signed
     */
    boolean isSigned();

    /**
     * Returns whether another integer type is capable of holding this type's value without losing
     * data. (Disregards signing.)
     *
     * @param other Other type
     * @return true if the other integer type has a larger size than this
     */
    boolean isWithinLimit(IntegerType other);

    /**
     * Returns whether another integer type has the same value range as this
     *
     * @param other Other type
     * @return true if the other integer type has the same size and signing as this
     */
    boolean isSameRange(IntegerType other);

    /**
     * The difference in size between another integer type and this, expressed as the distance
     * between their value ranges.
     *
     * @param other Other type
     * @return Ordinal distance between another type and this
     */
    double ordinalDistance(IntegerType other);
  }

  @Immutable
  interface DecimalType extends ImmutableType {
    /**
     * The size of this floating point type
     *
     * @return Size
     */
    int size();
  }

  @Immutable
  interface BooleanType extends ImmutableType {
    /**
     * The size of this boolean type
     *
     * @return Size
     */
    int size();
  }

  @Immutable
  interface TextType extends ImmutableType {}

  @Immutable
  interface VariantType extends ImmutableType {
    enum VariantKind {
      OLE_VARIANT,
      NORMAL_VARIANT
    }

    /**
     * The kind of variant that this type is
     *
     * @return Variant kind
     */
    VariantKind kind();
  }
}