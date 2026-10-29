package seedu.address.testutil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javafx.application.Platform;

/**
 * Shares one JavaFX runtime across UI test classes and stops it after the test suite.
 */
public class JavaFxExtension implements BeforeAllCallback {
    private static final int FX_TIMEOUT_SECONDS = 10;
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(JavaFxExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getRoot().getStore(NAMESPACE).getOrComputeIfAbsent(JavaFxRuntime.class,
                key -> new JavaFxRuntime(), JavaFxRuntime.class);
    }

    /**
     * Runs {@code action} on the JavaFX application thread and waits for it to finish.
     */
    public static void runOnFxThread(Runnable action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /** Keeps the toolkit alive until JUnit closes its root extension store. */
    private static class JavaFxRuntime implements AutoCloseable {
        JavaFxRuntime() {
            Platform.startup(() -> Platform.setImplicitExit(false));
        }

        @Override
        public void close() {
            Platform.exit();
        }
    }
}
