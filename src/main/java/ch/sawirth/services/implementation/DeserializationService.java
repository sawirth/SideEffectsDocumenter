package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.ClassRepresentation;
import ch.sawirth.services.IDeserializationService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class DeserializationService implements IDeserializationService {

    private final Gson gson;

    @Inject
    public DeserializationService(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Set<ClassRepresentation> deserializePuranoResult(String puranoJson) {
        Type collectionType = new TypeToken<HashSet<ClassRepresentation>>(){}.getType();
        return gson.fromJson(puranoJson, collectionType);
    }
}
