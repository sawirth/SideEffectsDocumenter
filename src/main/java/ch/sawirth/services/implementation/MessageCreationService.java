package ch.sawirth.services.implementation;

import ch.sawirth.model.purano.*;
import ch.sawirth.services.IMessageCreationService;
import ch.sawirth.utils.IODetectionHelper;
import com.github.javaparser.ast.body.Parameter;
import com.google.inject.Inject;
import main.binding.DoCreateHtmlTags;
import main.binding.DoCreateLinks;
import org.apache.commons.lang3.StringUtils;;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageCreationService implements IMessageCreationService {
    private final int indentionSpaces = 6;
    private final boolean doCreateLinks;
    private final boolean doCreateHtmlTags;

    @Inject
    public MessageCreationService(@DoCreateLinks boolean doCreateLinks, @DoCreateHtmlTags boolean doCreateHtmlTags) {
        this.doCreateLinks = doCreateLinks;
        this.doCreateHtmlTags = doCreateHtmlTags;
    }

    @Override
    public List<String> createArgumentModifierMessage(List<ArgumentModifier> argumentModifiers, List<Parameter> parameters) {
        List<String> result = new ArrayList<>();
        result.add("Modifies the following arguments:");
        argumentModifiers.sort((a1, a2) -> Boolean.compare(a1.isDynamicEffect, a2.isDynamicEffect));

        if (argumentModifiers.stream()
                .anyMatch(a -> !a.isDynamicEffect)
                && argumentModifiers.stream()
                .anyMatch(a -> a.isDynamicEffect)) {
            result.add(StringUtils.repeat(' ', indentionSpaces / 2) + "Static effects");
        }

        List<ArgumentModifier> staticEffects = argumentModifiers.stream()
                .filter(a -> !a.isDynamicEffect)
                .collect(Collectors.toList());

        List<ArgumentModifier> dynamicEffects = argumentModifiers.stream()
                .filter(a -> a.isDynamicEffect)
                .collect(Collectors.toList());

        List<String> modifierMessages = new ArrayList<>();
        for (ArgumentModifier modifier : staticEffects) {
            try {
                modifierMessages.add(createSingleArgumentModifierMessage(modifier, parameters, false));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (this.doCreateHtmlTags) {
            modifierMessages = convertToHtmlList(modifierMessages);
        }

        result.addAll(modifierMessages);

        if (argumentModifiers.stream().anyMatch(a -> a.isDynamicEffect)) {
            result.add(StringUtils.repeat(' ', indentionSpaces / 2) + "Dynamic effects (i.e. from subclasses)");
        }

        modifierMessages.clear();
        for (ArgumentModifier modifier : dynamicEffects) {
            try {
                modifierMessages.add(createSingleArgumentModifierMessage(modifier, parameters, true));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (this.doCreateHtmlTags) {
            modifierMessages = convertToHtmlList(modifierMessages);
        }

        result.addAll(modifierMessages);
        return result;
    }

    private String createSingleArgumentModifierMessage(ArgumentModifier modifier, List<Parameter> parameters, boolean isDynamic) throws IndexOutOfBoundsException {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.repeat(' ', indentionSpaces));
        String name = parameters.get(modifier.argumentIndex).getNameAsString();

        sb.append(name);
        if (!modifier.owner.isEmpty()) {
            String owner = getShortOwner(modifier.owner, ".");
            sb.append(String.format(" (via %s.%s",
                                    owner,
                                    modifier.name));

            String originOwner = getShortOwner(modifier.originOwner, ".");
            if (originOwner.equals(owner)) {
                sb.append(")");
            } else {
                sb.append(String.format(" - origin: %s.%s)", originOwner, modifier.originName));
            }
        }

        return sb.toString();
    }

    @Override
    public List<String> createStaticModifiersMessage(List<FieldModifier> staticFieldModifiers) {
        List<String> result = new ArrayList<>();
        result.add("Modifies the following static fields:");

        List<String> modifierMessages = new ArrayList<>();
        for (FieldModifier modifier : staticFieldModifiers) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            sb.append(String.format("%s.%s (%s)" ,
                                    getShortOwner(modifier.owner, "/"),
                                    modifier.name,
                                    getShortOwner(modifier.type, "/")));

            modifierMessages.add(sb.toString());
        }

        if (this.doCreateHtmlTags) {
            modifierMessages = convertToHtmlList(modifierMessages);
        }

        result.addAll(modifierMessages);
        return result;
    }

    private List<String> convertToHtmlList(List<String> messages) {
        if (messages.size() <= 0) {
            return messages;
        }

        List<String> result = new ArrayList<>();
        result.add("<ul>");
        for (String msg : messages) {
            result.add("<li>" + msg + StringUtils.repeat(' ', indentionSpaces) + "</li>");
        }

        result.add("</ul>");
        return result;
    }

    @Override
    public List<String> createReturnDependencyMessage(
            ReturnDependency returnDependency,
            List<Parameter> parameters,
            boolean isAbstract,
            boolean isInterfaceMethod)
    {
        List<String> result = new ArrayList<>();
        result.add("Return value depends on the following:");

        List<String> messages = new ArrayList<>();
        for (Integer integer : returnDependency.indexOfDependentArguments) {
            String argumentType = "";
            try {
                argumentType = parameters.get(integer).getType().toString();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                continue;
            }

            String name = "";
            try {
                name = parameters.get(integer ).getNameAsString();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                continue;
            }

            FieldDependency correspondingFieldDep = getCorrespondingFieldDependencyOrNull(argumentType,
                                                                                          returnDependency.fieldDependencies);

            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat(' ', indentionSpaces));
            sb.append("Argument: ");
            if (correspondingFieldDep != null) {
                sb.append(String.format("%s.%s (%s)", name, correspondingFieldDep.name, getShortOwner(correspondingFieldDep.desc, ".")));

                //It has to be removed otherwise there would be duplicated information
                returnDependency.fieldDependencies.remove(correspondingFieldDep);
            } else {
                sb.append(String.format("%s (%s)", name, getShortOwner(argumentType, ".")));
            }

            messages.add(sb.toString());
        }

        for (FieldDependency fieldDependency : returnDependency.staticFieldDependencies) {
            messages.add(createFieldDependencyMessage(fieldDependency, true, isAbstract, isInterfaceMethod));
        }

        for (FieldDependency fieldDependency : returnDependency.fieldDependencies) {
            messages.add(createFieldDependencyMessage(fieldDependency, false, isAbstract, isInterfaceMethod));
        }

        if (this.doCreateHtmlTags) {
            messages = convertToHtmlList(messages);
        }

        result.addAll(messages);
        return result;
    }

    @Override
    public List<String> createNativeEffectsMessage(Set<NativeEffect> nativeEffectSet, Set<String> importAndPackageDeclarations) {
        List<String> result = new ArrayList<>();
        if (nativeEffectSet.stream().allMatch(e -> e.isDynamicEffect)) {
            result.add("The method might call native code depending on the actual type:");
        } else {
            result.add("The method calls native code:");
        }

        Set<NativeEffect> staticEffects = nativeEffectSet.stream().filter(e -> !e.isDynamicEffect).collect(Collectors.toSet());
        Set<NativeEffect> dynamicEffects = nativeEffectSet.stream().filter(e -> e.isDynamicEffect).collect(Collectors.toSet());

        if (staticEffects.size() > 0 && dynamicEffects.size() > 0) {
            result.add(StringUtils.repeat(' ', indentionSpaces / 2) + "Static effects");
        }

        List<String> messages = new ArrayList<>();
        for (NativeEffect effect : staticEffects) {
            messages.add(createSingleNativeEffectMessage(effect, importAndPackageDeclarations));
        }

        if (this.doCreateHtmlTags) {
            messages = convertToHtmlList(messages);
        }

        result.addAll(messages);
        messages.clear();

        if (dynamicEffects.size() > 0) {
            result.add(StringUtils.repeat(' ', indentionSpaces / 2) + "Dynamic effects (i.e. from subclasses)");
        }

        for (NativeEffect effect : dynamicEffects) {
            messages.add(createSingleNativeEffectMessage(effect, importAndPackageDeclarations));
        }

        if (this.doCreateHtmlTags) {
            messages = convertToHtmlList(messages);
        }

        result.addAll(messages);
        return result;
    }

    private String createSingleNativeEffectMessage(NativeEffect nativeEffect, Set<String> importAndPackageDeclarations) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.repeat(' ', indentionSpaces));

        boolean writeShortOwnerInLink = writeShortOwner(nativeEffect.owner, importAndPackageDeclarations);
        String message = createNativeMessage(nativeEffect.owner, nativeEffect.name, writeShortOwnerInLink);
        sb.append(message);

        boolean addParenthesis = false;
        String identifier = getShortOwner(nativeEffect.owner, ".") + "." + nativeEffect.name;
        if (!nativeEffect.originOwner.isEmpty() && !nativeEffect.originName.isEmpty()
                && !identifier.equals(getShortOwner(nativeEffect.originOwner, ".") + "." + nativeEffect.originName))
        {
            boolean writeShortOriginOwnerLink = writeShortOwner(nativeEffect.originOwner, importAndPackageDeclarations);
            String originMessage = " (origin: " + createNativeMessage(nativeEffect.originOwner, nativeEffect.originName, writeShortOriginOwnerLink);
            sb.append(originMessage);
            addParenthesis = true;
        }

        String fullQualifier = nativeEffect.owner + "." + nativeEffect.name;
        String originFullQualifierName = nativeEffect.originOwner + "." + nativeEffect.originName;
        if (IODetectionHelper.getInstance().isPossibleIO(fullQualifier)
                || IODetectionHelper.getInstance().isPossibleIO(originFullQualifierName)) {
            sb.append(" - Possible I/O");
        }

        if (addParenthesis){
            sb.append(")");
        }

        return sb.toString();
    }

    private boolean writeShortOwner(String owner, Set<String> importAndPackageDeclarations) {
        return importAndPackageDeclarations.contains(owner)
                || importAndPackageDeclarations.stream().anyMatch(owner::contains);
    }

    private String createNativeMessage(String owner, String methodName, boolean writeShortOwnerLink) {
        if (methodName.contains("<init>")) {
            methodName = getShortOwner(owner, ".") + "()";
        }

        return this.doCreateLinks
                ? "{@link " +
                (writeShortOwnerLink
                        ? getShortOwner(owner, ".")
                        : owner)
                    + "#" + methodName + "}"
                : getShortOwner(owner, ".") + "." + methodName;
    }

    private String createFieldDependencyMessage(FieldDependency fieldDependency, boolean isStaticField, boolean isAbstract, boolean isInterface) {
        String title = "Field";

        if (isStaticField) {
            title = "Static Field";
        }else if (isInterface){
            title = "Field of implementation";
        } else if (!fieldDependency.isThisField) {
            title = "Field of subclass";
        }

        String owner = (isStaticField || isAbstract || !fieldDependency.isThisField)
                ? getShortOwner(fieldDependency.owner, ".")
                : "this";

        return StringUtils.repeat(' ', indentionSpaces) +
                title +
                ": " +
                String.format("%s.%s (%s)",
                              owner,
                              fieldDependency.name,
                              fieldDependency.desc);
    }

    private FieldDependency getCorrespondingFieldDependencyOrNull(String ownerName, Set<FieldDependency> fieldDependencies) {
        List<FieldDependency> correspondingFieldDeps = fieldDependencies.stream()
                .filter(d -> d.owner.contains(ownerName))
                .collect(Collectors.toList());

        if (correspondingFieldDeps.size() == 1) {
            return correspondingFieldDeps.get(0);
        }

        return null;
    }

    private String getShortOwner(String owner, String separator) {
        if (owner.contains(separator)) {
            return StringUtils.substringAfterLast(owner, separator);
        }

        return owner;
    }

    /**
     *
     * @param a
     * @return
     *
     * Modifies the following arguments
     *   Static effects
     *       test
     *         test
     *         test
     *     Dynamic effects
     *         test
     *         test
     *         test
     *
     * Modifies the following static fields
     *     test
     *     test
     *     test
     *
     * Return value depends on
     *     Static field: Class.test (int)
     *     Argument: param (int)
     *     Argument: rectangle.height (int)
     *     Field: this.field
     *     Field:
     */
    private int example(int a) {
        return 5;
    }
}
