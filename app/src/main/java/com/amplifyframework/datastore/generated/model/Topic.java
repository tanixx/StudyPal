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

/** This is an auto generated class representing the Topic type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Topics", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "undefined", fields = {"id"})
@Index(name = "bySubject", fields = {"subjectID"})
public final class Topic implements Model {
  public static final QueryField ID = field("Topic", "id");
  public static final QueryField SUBJECT_ID = field("Topic", "subjectID");
  public static final QueryField TOPIC_NAME = field("Topic", "topicName");
  public static final QueryField DEADLINE = field("Topic", "deadline");
  public static final QueryField PROGRESS = field("Topic", "progress");
  public static final QueryField IMPORTANCE = field("Topic", "importance");
  public static final QueryField OWNER = field("Topic", "owner");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String subjectID;
  private final @ModelField(targetType="String", isRequired = true) String topicName;
  private final @ModelField(targetType="String") String deadline;
  private final @ModelField(targetType="String") String progress;
  private final @ModelField(targetType="String") String importance;
  private final @ModelField(targetType="String", isRequired = true) String owner;
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
  
  public String getSubjectId() {
      return subjectID;
  }
  
  public String getTopicName() {
      return topicName;
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
  
  public String getOwner() {
      return owner;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Topic(String id, String subjectID, String topicName, String deadline, String progress, String importance, String owner) {
    this.id = id;
    this.subjectID = subjectID;
    this.topicName = topicName;
    this.deadline = deadline;
    this.progress = progress;
    this.importance = importance;
    this.owner = owner;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Topic topic = (Topic) obj;
      return ObjectsCompat.equals(getId(), topic.getId()) &&
              ObjectsCompat.equals(getSubjectId(), topic.getSubjectId()) &&
              ObjectsCompat.equals(getTopicName(), topic.getTopicName()) &&
              ObjectsCompat.equals(getDeadline(), topic.getDeadline()) &&
              ObjectsCompat.equals(getProgress(), topic.getProgress()) &&
              ObjectsCompat.equals(getImportance(), topic.getImportance()) &&
              ObjectsCompat.equals(getOwner(), topic.getOwner()) &&
              ObjectsCompat.equals(getCreatedAt(), topic.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), topic.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSubjectId())
      .append(getTopicName())
      .append(getDeadline())
      .append(getProgress())
      .append(getImportance())
      .append(getOwner())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Topic {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("subjectID=" + String.valueOf(getSubjectId()) + ", ")
      .append("topicName=" + String.valueOf(getTopicName()) + ", ")
      .append("deadline=" + String.valueOf(getDeadline()) + ", ")
      .append("progress=" + String.valueOf(getProgress()) + ", ")
      .append("importance=" + String.valueOf(getImportance()) + ", ")
      .append("owner=" + String.valueOf(getOwner()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static SubjectIdStep builder() {
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
  public static Topic justId(String id) {
    return new Topic(
      id,
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
      subjectID,
      topicName,
      deadline,
      progress,
      importance,
      owner);
  }
  public interface SubjectIdStep {
    TopicNameStep subjectId(String subjectId);
  }
  

  public interface TopicNameStep {
    OwnerStep topicName(String topicName);
  }
  

  public interface OwnerStep {
    BuildStep owner(String owner);
  }
  

  public interface BuildStep {
    Topic build();
    BuildStep id(String id);
    BuildStep deadline(String deadline);
    BuildStep progress(String progress);
    BuildStep importance(String importance);
  }
  

  public static class Builder implements SubjectIdStep, TopicNameStep, OwnerStep, BuildStep {
    private String id;
    private String subjectID;
    private String topicName;
    private String owner;
    private String deadline;
    private String progress;
    private String importance;
    public Builder() {
      
    }
    
    private Builder(String id, String subjectID, String topicName, String deadline, String progress, String importance, String owner) {
      this.id = id;
      this.subjectID = subjectID;
      this.topicName = topicName;
      this.deadline = deadline;
      this.progress = progress;
      this.importance = importance;
      this.owner = owner;
    }
    
    @Override
     public Topic build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Topic(
          id,
          subjectID,
          topicName,
          deadline,
          progress,
          importance,
          owner);
    }
    
    @Override
     public TopicNameStep subjectId(String subjectId) {
        Objects.requireNonNull(subjectId);
        this.subjectID = subjectId;
        return this;
    }
    
    @Override
     public OwnerStep topicName(String topicName) {
        Objects.requireNonNull(topicName);
        this.topicName = topicName;
        return this;
    }
    
    @Override
     public BuildStep owner(String owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
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
    private CopyOfBuilder(String id, String subjectId, String topicName, String deadline, String progress, String importance, String owner) {
      super(id, subjectID, topicName, deadline, progress, importance, owner);
      Objects.requireNonNull(subjectID);
      Objects.requireNonNull(topicName);
      Objects.requireNonNull(owner);
    }
    
    @Override
     public CopyOfBuilder subjectId(String subjectId) {
      return (CopyOfBuilder) super.subjectId(subjectId);
    }
    
    @Override
     public CopyOfBuilder topicName(String topicName) {
      return (CopyOfBuilder) super.topicName(topicName);
    }
    
    @Override
     public CopyOfBuilder owner(String owner) {
      return (CopyOfBuilder) super.owner(owner);
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
  

  public static class TopicIdentifier extends ModelIdentifier<Topic> {
    private static final long serialVersionUID = 1L;
    public TopicIdentifier(String id) {
      super(id);
    }
  }
  
}
