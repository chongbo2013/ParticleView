package com.github.shchurov.particleview.sample.burst;

import android.graphics.PointF;

import com.github.shchurov.particleview.Particle;
import com.github.shchurov.particleview.ParticleSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

class BurstParticleSystem implements ParticleSystem {

    private static final int MAX_P_COUNT = 3000;
    private static final int BURST_P_COUNT = 100;
    private static final int BURST_DURATION = 2;
    private static final double FADE_DURATION = 0.4;
    private static final int MAX_VY = 500;
    private static final int MAX_VX = 500;
    private static final int MAX_VR = 10;

    private List<BurstParticle> particles = new ArrayList<>();
    private Queue<PointF> originsQueue = new ConcurrentLinkedQueue<>();
    private Random random = new Random();

    @Override
    public int getMaxCount() {
        return MAX_P_COUNT;
    }

    @Override
    public List<? extends Particle> getParticles() {
        return particles;
    }

    void addBurst(float x, float y) {
        originsQueue.add(new PointF(x, y));
    }

    @Override
    public void update(double timeDelta) {
        updateParticles(timeDelta);
        pollOrigins();
    }

    private void updateParticles(double timeDelta) {
        for (int i = 0; i < particles.size(); i++) {
            BurstParticle p = particles.get(i);
            if ((p.timeLeft -= timeDelta) < 0) {
                particles.remove(i--);
                continue;
            }
            p.setX(p.getX() + (float) (p.vx * timeDelta));
            p.setY(p.getY() + (float) (p.vy * timeDelta));
            p.setRotation(p.getRotation() + (float) (p.vr * timeDelta));
            p.setAlpha(Math.min(1f, (float) (p.timeLeft / FADE_DURATION)));
        }
    }

    private void pollOrigins() {
        while (originsQueue.size() > 0) {
            PointF origin = originsQueue.poll();
            int n = Math.min(BURST_P_COUNT, MAX_P_COUNT - particles.size());
            for (int i = 0; i < n; i++) {
                float vx = (random.nextBoolean() ? 1 : -1) * MAX_VX * random.nextFloat();
                float vy = (random.nextBoolean() ? 1 : -1) * MAX_VY * random.nextFloat();
                float vr = (random.nextBoolean() ? 1 : -1) * MAX_VR * random.nextFloat();
                particles.add(new BurstParticle(origin.x, origin.y, random.nextInt(16), vx, vy, vr, BURST_DURATION));
            }
        }
    }

}