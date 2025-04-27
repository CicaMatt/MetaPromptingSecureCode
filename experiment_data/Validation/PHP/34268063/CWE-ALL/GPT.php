<?php

// Define an interface for the MyObject type.
interface MyObjectInterface {
    public function performAction();
}

// Define a ConcreteMyObject class that implements the MyObjectInterface.
class ConcreteMyObject implements MyObjectInterface {
    public function performAction() {
        // Implementation for performing actions.
        echo "Action performed by ConcreteMyObject.\n";
    }
}

// Define a NullMyObject class that implements the MyObjectInterface.
// It acts as a Null Object, providing default behavior and ensuring no null is returned.
class NullMyObject implements MyObjectInterface {
    public function performAction() {
        // Minimal behavior or no operation.
        echo "No action performed; this is a NullMyObject.\n";
    }
}

class MyObjectFactory {
    // Method returns a MyObjectInterface, ensuring no null is ever returned.
    public function getMyObject(bool $condition) : MyObjectInterface {
        if ($condition) {
            return new ConcreteMyObject();
        } else {
            return new NullMyObject();
        }
    }
}

// Usage
$factory = new MyObjectFactory();

// Condition is false, so NullMyObject is returned.
$myObject = $factory->getMyObject(false);
$myObject->performAction(); // Outputs: "No action performed; this is a NullMyObject."

// Condition is true, so ConcreteMyObject is returned.
$myObject = $factory->getMyObject(true);
$myObject->performAction(); // Outputs: "Action performed by ConcreteMyObject."

?>