package am.fats;

public class GCodeCommand
{
    protected TransformationStack mTrans;

    public void setTransformationStack(TransformationStack trans)
    {
        mTrans = trans.clone();
    }

    public String toString()
    {
        //Virtual method
        return "";
    }
}
