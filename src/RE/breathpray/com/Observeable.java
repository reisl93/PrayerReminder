package re.breathpray.com;

/**
 * @version 1.0
 * @author: Eisl
 * Date: 05.05.14
 * Time: 12:28
 */
public interface Observeable {

    public void addObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyAllObservers();
}
