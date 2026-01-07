package at.koopro.spells_n_squares.features.fx.base;

/**
 * Base implementation of Effect interface.
 * Provides common functionality for effect lifecycle management.
 */
public abstract class BaseEffect implements Effect {
    protected final String id;
    protected final float intensity;
    protected final int duration;
    protected int age;
    protected boolean stopped;
    
    protected BaseEffect(String id, float intensity, int duration) {
        this.id = id;
        this.intensity = intensity;
        this.duration = duration;
        this.age = 0;
        this.stopped = false;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public float getIntensity() {
        return intensity;
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public int getAge() {
        return age;
    }
    
    @Override
    public void tick() {
        if (!stopped) {
            age++;
        }
    }
    
    @Override
    public boolean isExpired() {
        return stopped || (duration >= 0 && age >= duration);
    }
    
    @Override
    public float getCurrentIntensity() {
        if (stopped) {
            return 0.0f;
        }
        // For infinite duration, return full intensity
        if (duration < 0) {
            return intensity;
        }
        // Fade out over time
        float progress = (float) age / duration;
        return intensity * (1.0f - progress);
    }
    
    @Override
    public void stop() {
        stopped = true;
    }
}









