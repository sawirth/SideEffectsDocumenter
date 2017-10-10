package ch.sawirth.services;

import ch.sawirth.model.purano.ClassRepresentation;

import java.util.Set;

public interface IDeserializationService {
    Set<ClassRepresentation> deserializePuranoResult(String puranoJson);
}
