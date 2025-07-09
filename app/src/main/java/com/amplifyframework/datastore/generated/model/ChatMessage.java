package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the ChatMessage type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ChatMessages", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class ChatMessage implements Model {
  public static final QueryField ID = field("ChatMessage", "id");
  public static final QueryField USER = field("ChatMessage", "userChatsId");
  public static final QueryField MESSAGE = field("ChatMessage", "message");
  public static final QueryField RESPONSE = field("ChatMessage", "response");
  public static final QueryField TIMESTAMP = field("ChatMessage", "timestamp");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userChatsId", targetNames = {"userChatsId"}, type = User.class) User user;
  private final @ModelField(targetType="String", isRequired = true) String message;
  private final @ModelField(targetType="String") String response;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime timestamp;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public User getUser() {
      return user;
  }
  
  public String getMessage() {
      return message;
  }
  
  public String getResponse() {
      return response;
  }
  
  public Temporal.DateTime getTimestamp() {
      return timestamp;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private ChatMessage(String id, User user, String message, String response, Temporal.DateTime timestamp) {
    this.id = id;
    this.user = user;
    this.message = message;
    this.response = response;
    this.timestamp = timestamp;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ChatMessage chatMessage = (ChatMessage) obj;
      return ObjectsCompat.equals(getId(), chatMessage.getId()) &&
              ObjectsCompat.equals(getUser(), chatMessage.getUser()) &&
              ObjectsCompat.equals(getMessage(), chatMessage.getMessage()) &&
              ObjectsCompat.equals(getResponse(), chatMessage.getResponse()) &&
              ObjectsCompat.equals(getTimestamp(), chatMessage.getTimestamp()) &&
              ObjectsCompat.equals(getCreatedAt(), chatMessage.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), chatMessage.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUser())
      .append(getMessage())
      .append(getResponse())
      .append(getTimestamp())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ChatMessage {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("message=" + String.valueOf(getMessage()) + ", ")
      .append("response=" + String.valueOf(getResponse()) + ", ")
      .append("timestamp=" + String.valueOf(getTimestamp()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static MessageStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static ChatMessage justId(String id) {
    return new ChatMessage(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user,
      message,
      response,
      timestamp);
  }
  public interface MessageStep {
    TimestampStep message(String message);
  }
  

  public interface TimestampStep {
    BuildStep timestamp(Temporal.DateTime timestamp);
  }
  

  public interface BuildStep {
    ChatMessage build();
    BuildStep id(String id);
    BuildStep user(User user);
    BuildStep response(String response);
  }
  

  public static class Builder implements MessageStep, TimestampStep, BuildStep {
    private String id;
    private String message;
    private Temporal.DateTime timestamp;
    private User user;
    private String response;
    public Builder() {
      
    }
    
    private Builder(String id, User user, String message, String response, Temporal.DateTime timestamp) {
      this.id = id;
      this.user = user;
      this.message = message;
      this.response = response;
      this.timestamp = timestamp;
    }
    
    @Override
     public ChatMessage build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ChatMessage(
          id,
          user,
          message,
          response,
          timestamp);
    }
    
    @Override
     public TimestampStep message(String message) {
        Objects.requireNonNull(message);
        this.message = message;
        return this;
    }
    
    @Override
     public BuildStep timestamp(Temporal.DateTime timestamp) {
        Objects.requireNonNull(timestamp);
        this.timestamp = timestamp;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = user;
        return this;
    }
    
    @Override
     public BuildStep response(String response) {
        this.response = response;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, User user, String message, String response, Temporal.DateTime timestamp) {
      super(id, user, message, response, timestamp);
      Objects.requireNonNull(message);
      Objects.requireNonNull(timestamp);
    }
    
    @Override
     public CopyOfBuilder message(String message) {
      return (CopyOfBuilder) super.message(message);
    }
    
    @Override
     public CopyOfBuilder timestamp(Temporal.DateTime timestamp) {
      return (CopyOfBuilder) super.timestamp(timestamp);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder response(String response) {
      return (CopyOfBuilder) super.response(response);
    }
  }
  

  public static class ChatMessageIdentifier extends ModelIdentifier<ChatMessage> {
    private static final long serialVersionUID = 1L;
    public ChatMessageIdentifier(String id) {
      super(id);
    }
  }
  
}
