package formality.model;

/**
 * This interface allows a student model object to communicate
 * with a controller.
 */
public interface ModelListener {
    /**
     * This method is called to pass in any objects required
     * to initialize the model, DB accessors, for example.
     * @param initItems
     * @throws Exception
   */
    public void init(Object[] initItems) throws Exception;

        /**
    * Get the class name of this model
    * @param e a ModelEvent object
    * @throws Exception
   */
    public void updateModel(ModelEvent e) throws Exception;

    /**
     * This method allows the controller to get information
     * from the controller.
     * @return Object a class encapsulating the representation
    */
    public Object getModelData() throws Exception;

    /**
    * This method allows the controller to get information
    * from the controller using query information.
    * @param queryInfo an Object containing query terms.
    * @return Object a class encapsulating the representation.
 */
    public Object getModelData(Object queryInfo) throws Exception;

}
