package com.inswave.whive.common.builderqueue;

import java.util.List;

public interface BuilderQueueManagedgDao {

    void insert(BuilderQueueManaged builderQueueManaged);

    void update(BuilderQueueManaged builderQueueManaged);

    void projectUpdate(BuilderQueueManaged builderQueueManaged);

    void deployUpdate(BuilderQueueManaged builderQueueManaged);

    void etcUpdate(BuilderQueueManaged builderQueueManaged);

    void clusterIdUpdate(BuilderQueueManaged builderQueueManaged);

    BuilderQueueManaged findByID(Long builder_id);

    List<BuilderQueueManaged> findByAll();

}
