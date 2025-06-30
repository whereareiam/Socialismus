package me.whereareiam.socialismus.api.input.serializer;

import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;

/**
 * Interface for processing serialization tasks in the worker chain.
 * Extends the worker processor to handle serializer content specifically.
 *
 * <p>This interface is part of the serialization pipeline that processes
 * and transforms {@link SerializerContent} objects. It works in conjunction
 * with the {@link ComponentService} to format and serialize content.</p>
 */
public interface SerializationWorker extends WorkerProcessor<SerializerContent> {
}
