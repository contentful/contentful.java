package com.contentful.java.cda;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.type.SimpleType;
import java.util.Collection;

/**
 * Allows using nested "dot" dyntax for type discriminators.
 *
 */
public class NestedTypeResolver extends StdTypeResolverBuilder {
  @Override
  public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType,
      Collection<NamedType> subtypes) {
    //Copied this code from parent class, StdTypeResolverBuilder with same method name
    TypeIdResolver idRes = idResolver(config, baseType,
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(baseType.getRawClass()).build(),
        subtypes, false,
        true);
    return new NestedTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible,
        SimpleType.constructUnsafe(Object.class), _includeAs);
  }
}