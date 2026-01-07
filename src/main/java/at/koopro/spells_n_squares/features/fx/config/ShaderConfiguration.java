package at.koopro.spells_n_squares.features.fx.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shader configuration that can be exported/imported.
 * Supports both JSON and text formats.
 */
public class ShaderConfiguration {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public enum ShaderType {
        UNIFIED,
        INDIVIDUAL
    }
    
    private ShaderType shaderType;
    private Identifier shaderId; // For individual shaders
    private Map<String, Float> parameters;
    
    public ShaderConfiguration() {
        this.parameters = new HashMap<>();
    }
    
    public ShaderConfiguration(ShaderType shaderType, Identifier shaderId, Map<String, Float> parameters) {
        this.shaderType = shaderType;
        this.shaderId = shaderId;
        this.parameters = new HashMap<>(parameters);
    }
    
    public ShaderType getShaderType() {
        return shaderType;
    }
    
    public void setShaderType(ShaderType shaderType) {
        this.shaderType = shaderType;
    }
    
    public Identifier getShaderId() {
        return shaderId;
    }
    
    public void setShaderId(Identifier shaderId) {
        this.shaderId = shaderId;
    }
    
    public Map<String, Float> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Float> parameters) {
        this.parameters = parameters;
    }
    
    public Float getParameter(String name) {
        return parameters.get(name);
    }
    
    public void setParameter(String name, Float value) {
        parameters.put(name, value);
    }
    
    /**
     * Exports configuration to JSON format.
     */
    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("shaderType", shaderType.name());
        if (shaderId != null) {
            json.addProperty("shaderId", shaderId.toString());
        }
        
        JsonObject params = new JsonObject();
        for (Map.Entry<String, Float> entry : parameters.entrySet()) {
            params.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("parameters", params);
        
        return GSON.toJson(json);
    }
    
    /**
     * Exports configuration to text format (key=value pairs).
     */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Shader Configuration\n");
        sb.append("shaderType=").append(shaderType.name()).append("\n");
        if (shaderId != null) {
            sb.append("shaderId=").append(shaderId.toString()).append("\n");
        }
        sb.append("\n# Parameters\n");
        for (Map.Entry<String, Float> entry : parameters.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Imports configuration from JSON format.
     */
    public static ShaderConfiguration fromJson(String jsonString) {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        ShaderConfiguration config = new ShaderConfiguration();
        
        if (json.has("shaderType")) {
            config.shaderType = ShaderType.valueOf(json.get("shaderType").getAsString());
        }
        
        if (json.has("shaderId")) {
            config.shaderId = Identifier.parse(json.get("shaderId").getAsString());
        }
        
        if (json.has("parameters")) {
            JsonObject params = json.getAsJsonObject("parameters");
            for (Map.Entry<String, com.google.gson.JsonElement> entry : params.entrySet()) {
                config.parameters.put(entry.getKey(), entry.getValue().getAsFloat());
            }
        }
        
        return config;
    }
    
    /**
     * Imports configuration from text format.
     */
    public static ShaderConfiguration fromText(String text) {
        ShaderConfiguration config = new ShaderConfiguration();
        
        for (String line : text.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            int eqIndex = line.indexOf('=');
            if (eqIndex > 0) {
                String key = line.substring(0, eqIndex).trim();
                String value = line.substring(eqIndex + 1).trim();
                
                if ("shaderType".equals(key)) {
                    config.shaderType = ShaderType.valueOf(value);
                } else if ("shaderId".equals(key)) {
                    config.shaderId = Identifier.parse(value);
                } else {
                    try {
                        config.parameters.put(key, Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        // Skip invalid parameter values
                    }
                }
            }
        }
        
        return config;
    }
    
    /**
     * Saves configuration to a file.
     */
    public void saveToFile(Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        
        String extension = getFileExtension(filePath);
        String content;
        
        if ("json".equalsIgnoreCase(extension)) {
            content = toJson();
        } else {
            content = toText();
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(content);
        }
    }
    
    /**
     * Loads configuration from a file.
     */
    public static ShaderConfiguration loadFromFile(Path filePath) throws IOException {
        String extension = getFileExtension(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            if ("json".equalsIgnoreCase(extension)) {
                return fromJson(content.toString());
            } else {
                return fromText(content.toString());
            }
        }
    }
    
    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "txt";
    }
}


