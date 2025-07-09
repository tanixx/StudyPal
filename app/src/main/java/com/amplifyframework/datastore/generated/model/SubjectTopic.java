package com.amplifyframework.datastore.generated.model;

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

/** This is an auto generated class representing the SubjectTopic type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "SubjectTopics", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class SubjectTopic implements Model {
  public static final QueryField ID = field("SubjectTopic", "id");
  public static final QueryField USERNAME = field("SubjectTopic", "username");
  public static final QueryField EMAIL = field("SubjectTopic", "email");
  public static final QueryField SUBJECT = field("SubjectTopic", "subject");
  public static final QueryField TOPIC = field("SubjectTopic", "topic");
  public static final QueryField DEADLINE = field("SubjectTopic", "deadline");
  public static final QueryField PROGRESS = field("SubjectTopic", "progress");
  public static final QueryField IMPORTANCE = field("SubjectTopic", "importance");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String username;
  private final @ModelField(targetType="String") String email;
  private final @ModelField(targetType="String") String subject;
  private final @ModelField(targetType="String") String topic;
  private final @ModelField(targetType="String") String deadline;
  private final @ModelField(targetType="String") String progress;
  private final @ModelField(targetType="String") String importance;
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
  
  public String getUsername() {
      return username;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getSubject() {
      return subject;
  }
  
  public String getTopic() {
      return topic;
  }
  
  public String getDeadline() {
      return deadline;
  }
  
  public String getProgress() {
      return progress;
  }
  
  public String getImportance() {
      return importance;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private SubjectTopic(String id, String username, String email, String subject, String topic, String deadline, String progress, String importance) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.subject = subject;
    this.topic = topic;
    this.deadline = deadline;
    this.progress = progress;
    this.importance = importance;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      SubjectTopic subjectTopic = (SubjectTopic) obj;
      return ObjectsCompat.equals(getId(), subjectTopic.getId()) &&
              ObjectsCompat.equals(getUsername(), subjectTopic.getUsername()) &&
              ObjectsCompat.equals(getEmail(), subjectTopic.getEmail()) &&
              ObjectsCompat.equals(getSubject(), subjectTopic.getSubject()) &&
              ObjectsCompat.equals(getTopic(), subjectTopic.getTopic()) &&
              ObjectsCompat.equals(getDeadline(), subjectTopic.getDeadline()) &&
              ObjectsCompat.equals(getProgress(), subjectTopic.getProgress()) &&
              ObjectsCompat.equals(getImportance(), subjectTopic.getImportance()) &&
              ObjectsCompat.equals(getCreatedAt(), subjectTopic.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), subjectTopic.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUsername())
      .append(getEmail())
      .append(getSubject())
      .append(getTopic())
      .append(getDeadline())
      .append(getProgress())
      .append(getImportance())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("SubjectTopic {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("username=" + String.valueOf(getUsername()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("subject=" + String.valueOf(getSubject()) + ", ")
      .append("topic=" + String.valueOf(getTopic()) + ", ")
      .append("deadline=" + String.valueOf(getDeadline()) + ", ")
      .append("progress=" + String.valueOf(getProgress()) + ", ")
      .append("importance=" + String.valueOf(getImportance()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
  public static SubjectTopic justId(String id) {
    return new SubjectTopic(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      username,
      email,
      subject,
      topic,
      deadline,
      progress,
      importance);
  }
  public interface BuildStep {
    SubjectTopic build();
    BuildStep id(String id);
    BuildStep username(String username);
    BuildStep email(String email);
    BuildStep subject(String subject);
    BuildStep topic(String topic);
    BuildStep deadline(String deadline);
    BuildStep progress(String progress);
    BuildStep importance(String importance);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String username;
    private String email;
    private String subject;
    private String topic;
    private String deadline;
    private String progress;
    private String importance;
    public Builder() {
      
    }
    
    private Builder(String id, String username, String email, String subject, String topic, String deadline, String progress, String importance) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.subject = subject;
      this.topic = topic;
      this.deadline = deadline;
      this.progress = progress;
      this.importance = importance;
    }
    
    @Override
     public SubjectTopic build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new SubjectTopic(
          id,
          username,
          email,
          subject,
          topic,
          deadline,
          progress,
          importance);
    }
    
    @Override
     public BuildStep username(String username) {
        this.username = username;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    @Override
     public BuildStep topic(String topic) {
        this.topic = topic;
        return this;
    }
    
    @Override
     public BuildStep deadline(String deadline) {
        this.deadline = deadline;
        return this;
    }
    
    @Override
     public BuildStep progress(String progress) {
        this.progress = progress;
        return this;
    }
    
    @Override
     public BuildStep importance(String importance) {
        this.importance = importance;
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
    private CopyOfBuilder(String id, String username, String email, String subject, String topic, String deadline, String progress, String importance) {
      super(id, username, email, subject, topic, deadline, progress, importance);
      
    }
    
    @Override
     public CopyOfBuilder username(String username) {
      return (CopyOfBuilder) super.username(username);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder subject(String subject) {
      return (CopyOfBuilder) super.subject(subject);
    }
    
    @Override
     public CopyOfBuilder topic(String topic) {
      return (CopyOfBuilder) super.topic(topic);
    }
    
    @Override
     public CopyOfBuilder deadline(String deadline) {
      return (CopyOfBuilder) super.deadline(deadline);
    }
    
    @Override
     public CopyOfBuilder progress(String progress) {
      return (CopyOfBuilder) super.progress(progress);
    }
    
    @Override
     public CopyOfBuilder importance(String importance) {
      return (CopyOfBuilder) super.importance(importance);
    }
  }
  

  public static class SubjectTopicIdentifier extends ModelIdentifier<SubjectTopic> {
    private static final long serialVersionUID = 1L;
    public SubjectTopicIdentifier(String id) {
      super(id);
    }
  }
  
}
