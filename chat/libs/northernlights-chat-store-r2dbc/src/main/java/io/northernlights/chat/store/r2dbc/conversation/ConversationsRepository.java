package io.northernlights.chat.store.r2dbc.conversation;

import io.northernlights.chat.store.r2dbc.conversation.model.ConversationModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversationsRepository extends R2dbcRepository<ConversationModel, UUID> {
}
