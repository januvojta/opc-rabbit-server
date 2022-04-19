package cz.januvojt.opcrabbitserver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface OpcInputs {

    public int getProgramNumber();

    public void setProgramNumber(int value);

    public boolean isDataReady();

    public void setDataReady(boolean value);

    public int getShelfId();

    public void setShelfId(int value);

    public int getRobotId();

    public void setRobotId(int value);
}