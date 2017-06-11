package testutil;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

public interface WithMockito {

    default <T> T mock(Class<T> classToMock) {
        return Mockito.mock(classToMock);
    }

    default <T> OngoingStubbing<T> when(T methodCall) {
        return Mockito.when(methodCall);
    }

    default <T> T any() {
        return Mockito.any();
    }

    default <T> T verify(T mock) {
        return Mockito.verify(mock);
    }

    default void verifyNoMoreInteractions(Object... mocks) {
        Mockito.verifyNoMoreInteractions(mocks);
    }
}
