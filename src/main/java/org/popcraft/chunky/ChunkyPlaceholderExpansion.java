package org.popcraft.chunky;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.popcraft.chunky.iterator.ChunkIterator;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public class ChunkyPlaceholderExpansion extends PlaceholderExpansion {
    private static final String IDENTIFIER = "chunky";
    private static final String AUTHOR = "pop4959";
    private static final String VERSION = "1.0.0";
    private static final String NAME = "Chunky";
    private final Chunky chunky;

    public ChunkyPlaceholderExpansion() {
        this.chunky = Bukkit.getPluginManager().getPlugin(NAME) instanceof final ChunkyBukkit chunkyPlugin ? chunkyPlugin.getChunky() : null;
    }

    @Override
    public @NonNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NonNull String getAuthor() {
        return AUTHOR;
    }

    @Override
    public @NonNull String getVersion() {
        return VERSION;
    }

    @Override
    public @NonNull String getName() {
        return NAME;
    }

    @Override
    public @NonNull String getRequiredPlugin() {
        return NAME;
    }

    @Override
    public @Nullable String onRequest(final OfflinePlayer player, final @NonNull String params) {
        if (chunky == null) {
            return null;
        }
        final String[] tokens = params.split("_");
        if (tokens.length < 4 || !"task".equals(tokens[0]) || !"pregen".equals(tokens[1])) {
            return null;
        }
        final String placeholder = tokens[2];
        final String world = String.join("_", Arrays.copyOfRange(tokens, 3, tokens.length));
        final GenerationTask runningTask = chunky.getGenerationTasks().get(world);
        final Optional<GenerationTask> task = Optional.ofNullable(runningTask).or(() -> chunky.getServer().getWorld(world).flatMap(w -> chunky.getConfig().loadTask(w)));
        final String parameter = switch (placeholder) {
            case "exists" -> Boolean.toString(task.isPresent());
            case "running" -> Boolean.toString(runningTask != null);
            case "cancelled" -> Boolean.toString(task.map(GenerationTask::isCancelled).orElse(false));
            case "centerx" ->
                    Double.toString(task.map(GenerationTask::getSelection).map(Selection::centerX).orElse(0D));
            case "centerz" ->
                    Double.toString(task.map(GenerationTask::getSelection).map(Selection::centerZ).orElse(0D));
            case "radius", "radiusx" ->
                    Double.toString(task.map(GenerationTask::getSelection).map(Selection::radiusX).orElse(0D));
            case "radiusz" ->
                    Double.toString(task.map(GenerationTask::getSelection).map(Selection::radiusZ).orElse(0D));
            case "count" -> Long.toString(task.map(GenerationTask::getCount).orElse(0L));
            case "pattern" -> task.map(GenerationTask::getChunkIterator).map(ChunkIterator::name).orElse("pattern");
            case "shape" -> task.map(GenerationTask::getSelection).map(Selection::shape).orElse("shape");
            case "time" -> Long.toString(task.map(GenerationTask::getTotalTime).orElse(0L));
            default -> null;
        };
        if (parameter != null) {
            return parameter;
        }
        if (runningTask == null) {
            return null;
        }
        final GenerationTask.Progress progress = runningTask.getProgress();
        return switch (placeholder) {
            case "world" -> progress.getWorld();
            case "chunks" -> Long.toString(progress.getChunkCount());
            case "complete" -> Boolean.toString(progress.isComplete());
            case "percent" -> Float.toString(progress.getPercentComplete());
            case "hours" -> Long.toString(progress.getHours());
            case "minutes" -> Long.toString(progress.getMinutes());
            case "seconds" -> Long.toString(progress.getSeconds());
            case "rate" -> Double.toString(progress.getRate());
            case "x" -> Integer.toString(progress.getChunkX());
            case "z" -> Integer.toString(progress.getChunkZ());
            default -> null;
        };
    }
}
