package io.northernlights.chat.store.r2dbc.conversation.model;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationDataModel.ConversationDataType;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class ConversationDataTypeConverter extends EnumWriteSupport<ConversationDataType> {
}
