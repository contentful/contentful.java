package com.contentful.java.cda;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.IOException;

/**
 * Heavy work to support {@link NestedTypeResolver}
 */
public class NestedTypeDeserializer extends AsPropertyTypeDeserializer {

  public NestedTypeDeserializer(JavaType bt,
      TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
      JavaType defaultImpl) {
    super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
  }

  public NestedTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName,
      boolean typeIdVisible,
      JavaType defaultImpl, JsonTypeInfo.As inclusion) {
    super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, inclusion);
  }

  public NestedTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
    super(src, property);
  }

  @Override
  public TypeDeserializer forProperty(BeanProperty prop) {
    return this;
    //return (prop == _property) ? this : new NestedTypeDeserializer(this, prop);
  }

  @Override
  public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws
      IOException {

    JsonNode originalNode = p.readValueAsTree();
    JsonNode node = originalNode;
    if (originalNode == null || originalNode.isNull() || originalNode.isEmpty()) {
      return null;
    }
    //_typePropertyName is the dot separated value of "property" in @JsonTypeInfo
    for (String property : _typePropertyName.split("\\.")) {
      //traverse down any nested properties
      JsonNode nestedProp = node.get(property);
      if (nestedProp == null) {
        ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME,
            "missing property '" + _typePropertyName + "' that is to contain type id  (for class "
                + baseTypeName() + ")");
        return null;
      }
      node = nestedProp;
    }

    String type = node.asText();
    if (type == null || type.length() == 0) {
      type = _baseType.getTypeName();
    }
    if (_baseType.isTypeOrSubTypeOf(SynchronizedSpace.class)) {
      type = "SynchronizedSpace";
    }
    JsonDeserializer<Object> deser = _findDeserializer(ctxt, type);
    //Since JsonParser is a forward-only operation and finding the "type" discriminator advanced
    //the pointer, we need to reset it
    //Got clues from https://www.dilipkumarg.com/dynamic-polymorphic-type-handling-jackson/
    JsonParser jsonParser = new TreeTraversingParser(originalNode, p.getCodec());
    if (jsonParser.getCurrentToken() == null) {
      jsonParser.nextToken();
    }
    return deser.deserialize(jsonParser, ctxt);
  }
}