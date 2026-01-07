package at.koopro.spells_n_squares.features.fx.processing;

import net.minecraft.client.renderer.PostChain;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for reflection-based uniform updates in PostChain.
 * Caches reflection objects for performance.
 */
public final class PostProcessingReflectionHelper {
    
    // Cached reflection objects for performance
    private static java.lang.reflect.Field cachedPassesField = null;
    private static java.lang.reflect.Method cachedUpdateUniformMethod = null;
    private static final Map<Class<?>, java.lang.reflect.Field> cachedProgramFields = new HashMap<>();
    private static final Map<Class<?>, java.lang.reflect.Method> cachedUniformMethods = new HashMap<>();
    private static final String[] UNIFORM_METHOD_NAMES = {"setUniform", "setFloat", "setFloatUniform", "uniform1f"};
    
    private PostProcessingReflectionHelper() {}
    
    /**
     * Attempts to update the Time uniform in a PostChain using reflection.
     * This is a workaround since PostChain doesn't expose a direct uniform setter API.
     * Reflection objects are cached for performance.
     * 
     * @param chain The PostChain to update
     * @param time The time value to set
     */
    public static void updatePostChainTimeUniform(PostChain chain, float time) {
        if (chain == null) {
            return;
        }
        
        try {
            // Try multiple reflection approaches to update Time uniform
            // Approach 1: Try to access passes and update uniforms in each pass
            if (cachedPassesField == null) {
                try {
                    cachedPassesField = chain.getClass().getDeclaredField("passes");
                    cachedPassesField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    // Field doesn't exist, try alternative approach
                    cachedPassesField = null;
                }
            }
            
            if (cachedPassesField != null) {
                try {
                    @SuppressWarnings("unchecked")
                    java.util.List<Object> passes = (java.util.List<Object>) cachedPassesField.get(chain);
                    
                    if (passes != null) {
                        for (Object pass : passes) {
                            try {
                                // Try to get shader program from pass (cache field per class)
                                Class<?> passClass = pass.getClass();
                                java.lang.reflect.Field programField = cachedProgramFields.get(passClass);
                                
                                if (programField == null) {
                                    try {
                                        programField = passClass.getDeclaredField("program");
                                        programField.setAccessible(true);
                                        cachedProgramFields.put(passClass, programField);
                                    } catch (NoSuchFieldException e) {
                                        // Field doesn't exist for this class
                                        continue;
                                    }
                                }
                                
                                Object program = programField.get(pass);
                                if (program != null) {
                                    // Try to update Time uniform
                                    updateShaderProgramUniform(program, "Time", time);
                                }
                            } catch (Exception e) {
                                // Try next pass
                            }
                        }
                    }
                } catch (Exception e) {
                    // Reflection approach failed, try alternative method
                }
            }
        } catch (Exception e) {
            // Try alternative approach
        }
        
        // Approach 2: Try direct method access (cache method)
        if (cachedUpdateUniformMethod == null) {
            try {
                cachedUpdateUniformMethod = chain.getClass().getDeclaredMethod("updateUniform", String.class, float.class);
                cachedUpdateUniformMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Method doesn't exist
                cachedUpdateUniformMethod = null;
            }
        }
        
        if (cachedUpdateUniformMethod != null) {
            try {
                cachedUpdateUniformMethod.invoke(chain, "Time", time);
            } catch (Exception e) {
                // All reflection approaches failed - shader will use Time=0.0 (static pattern)
                // This is expected and shaders have fallback position-based calculations
            }
        }
    }
    
    /**
     * Helper method to update a uniform in a shader program using reflection.
     * Reflection Method objects are cached per class for performance.
     */
    private static void updateShaderProgramUniform(Object program, String uniformName, float value) {
        if (program == null) {
            return;
        }
        
        Class<?> programClass = program.getClass();
        
        // Check cache first
        java.lang.reflect.Method method = cachedUniformMethods.get(programClass);
        if (method != null) {
            try {
                method.invoke(program, uniformName, value);
                return; // Success
            } catch (Exception e) {
                // Cached method failed, try to find a new one
                cachedUniformMethods.remove(programClass);
            }
        }
        
        // Try various method names for setting float uniforms
        for (String methodName : UNIFORM_METHOD_NAMES) {
            try {
                method = programClass.getMethod(methodName, String.class, float.class);
                method.invoke(program, uniformName, value);
                // Cache successful method
                cachedUniformMethods.put(programClass, method);
                return; // Success
            } catch (NoSuchMethodException e) {
                // Try next method name
            } catch (Exception e) {
                // Method exists but invocation failed, try next
            }
        }
    }
    
    /**
     * Updates an arbitrary uniform in a PostChain by name.
     * Supports both direct uniforms and uniform blocks (std140 layout).
     * 
     * @param chain The PostChain to update
     * @param uniformName The name of the uniform (can be "UniformName" or "BlockName.FieldName")
     * @param value The float value to set
     */
    public static void updatePostChainUniform(PostChain chain, String uniformName, float value) {
        if (chain == null) {
            return;
        }
        
        try {
            // Try multiple reflection approaches
            // Approach 1: Try to access passes and update uniforms in each pass
            if (cachedPassesField == null) {
                try {
                    cachedPassesField = chain.getClass().getDeclaredField("passes");
                    cachedPassesField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    cachedPassesField = null;
                }
            }
            
            if (cachedPassesField != null) {
                try {
                    @SuppressWarnings("unchecked")
                    java.util.List<Object> passes = (java.util.List<Object>) cachedPassesField.get(chain);
                    
                    if (passes != null) {
                        for (Object pass : passes) {
                            try {
                                Class<?> passClass = pass.getClass();
                                java.lang.reflect.Field programField = cachedProgramFields.get(passClass);
                                
                                if (programField == null) {
                                    try {
                                        programField = passClass.getDeclaredField("program");
                                        programField.setAccessible(true);
                                        cachedProgramFields.put(passClass, programField);
                                    } catch (NoSuchFieldException e) {
                                        continue;
                                    }
                                }
                                
                                Object program = programField.get(pass);
                                if (program != null) {
                                    // Try to update uniform
                                    updateShaderProgramUniform(program, uniformName, value);
                                    
                                    // Also try with uniform block syntax (BlockName.FieldName)
                                    if (uniformName.contains(".")) {
                                        // Already in block format
                                    } else {
                                        // Try common uniform block names
                                        String[] blockNames = {"UnifiedConfig", "GrayscaleConfig", "VignetteConfig", 
                                                               "BloomConfig", "ContrastBoostConfig"};
                                        for (String blockName : blockNames) {
                                            updateShaderProgramUniform(program, blockName + "." + uniformName, value);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Try next pass
                            }
                        }
                    }
                } catch (Exception e) {
                    // Reflection approach failed, try alternative method
                }
            }
        } catch (Exception e) {
            // Try alternative approach
        }
        
        // Approach 2: Try direct method access
        if (cachedUpdateUniformMethod == null) {
            try {
                cachedUpdateUniformMethod = chain.getClass().getDeclaredMethod("updateUniform", String.class, float.class);
                cachedUpdateUniformMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                cachedUpdateUniformMethod = null;
            }
        }
        
        if (cachedUpdateUniformMethod != null) {
            try {
                cachedUpdateUniformMethod.invoke(chain, uniformName, value);
            } catch (Exception e) {
                // Method invocation failed
            }
        }
    }
}

