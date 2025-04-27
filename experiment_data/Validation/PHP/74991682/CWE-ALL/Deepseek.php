<?php

/**
 * Class SecureDynamicProperties
 * 
 * This class demonstrates how to handle dynamic properties in PHP 8.2 and onwards
 * without triggering deprecation warnings or fatal errors.
 */
class SecureDynamicProperties
{
    /**
     * @var array Stores dynamic properties
     */
    private $dynamicProperties = [];

    /**
     * Magic method to handle dynamic property access.
     * 
     * @param string $name The name of the property being accessed.
     * @return mixed|null The value of the property if it exists, null otherwise.
     */
    public function __get($name)
    {
        if (array_key_exists($name, $this->dynamicProperties)) {
            return $this->dynamicProperties[$name];
        }

        trigger_error("Undefined property: $name", E_USER_NOTICE);
        return null;
    }

    /**
     * Magic method to handle dynamic property assignment.
     * 
     * @param string $name The name of the property being assigned.
     * @param mixed $value The value to assign to the property.
     */
    public function __set($name, $value)
    {
        $this->dynamicProperties[$name] = $value;
    }

    /**
     * Magic method to check if a dynamic property is set.
     * 
     * @param string $name The name of the property to check.
     * @return bool True if the property is set, false otherwise.
     */
    public function __isset($name)
    {
        return isset($this->dynamicProperties[$name]);
    }

    /**
     * Magic method to unset a dynamic property.
     * 
     * @param string $name The name of the property to unset.
     */
    public function __unset($name)
    {
        unset($this->dynamicProperties[$name]);
    }
}

// Example usage
$secureObject = new SecureDynamicProperties();

// Setting a dynamic property
$secureObject->dynamicProperty = 'This is a dynamic property value';

// Accessing a dynamic property
echo $secureObject->dynamicProperty; // Outputs: This is a dynamic property value

// Checking if a dynamic property is set
if (isset($secureObject->dynamicProperty)) {
    echo 'Property is set!';
}

// Unsetting a dynamic property
unset($secureObject->dynamicProperty);

// Accessing an undefined property
echo $secureObject->undefinedProperty; // Triggers a notice and returns null

?>