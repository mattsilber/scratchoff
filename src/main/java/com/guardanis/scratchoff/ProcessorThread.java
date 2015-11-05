package com.guardanis.scratchoff;

public abstract class ProcessorThread extends Thread {

    private boolean active = false;

    @Override
    public void start(){
        this.active = true;
        super.start();
    }

    @Override
    public void run(){
        try{
            doInBackground();
        }
        catch(Throwable e){ e.printStackTrace(); }
    }

    protected abstract void doInBackground() throws Exception;

    public void cancel(){
        this.active = false;

        try{
            stop();
        }
        catch(Exception e){ }
    }

    public boolean isActive(){
        return active;
    }

}
