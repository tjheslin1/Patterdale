package testutil;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;

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

    default Long anyLong() {
        return Mockito.anyLong();
    }

    default <T> T eq(T t) {
        return Mockito.eq(t);
    }

    default <T> T verify(T mock) {
        return Mockito.verify(mock);
    }

    default <T> T verify(T mock, VerificationMode mode) {
        return Mockito.verify(mock, mode);
    }

    default void verifyNoMoreInteractions(Object... mocks) {
        Mockito.verifyNoMoreInteractions(mocks);
    }
}
