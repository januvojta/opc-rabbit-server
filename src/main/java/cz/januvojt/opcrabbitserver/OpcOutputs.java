package cz.januvojt.opcrabbitserver;

public interface OpcOutputs {
    boolean getOperationStarted();


    void setOperationStarted(boolean value);

    boolean getOperationFinished();

    void setOperationFinished(boolean value);

    int getErrorNumber();

    void setErrorNumber(int value);

    String getErrorString();

    void setErrorString(String value);

    String getOperationState();

    void setOperationState(String state);

    public boolean isDataReady();

    public void setDataReady(boolean value);
}
