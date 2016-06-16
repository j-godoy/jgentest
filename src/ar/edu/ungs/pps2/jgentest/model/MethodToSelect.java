package ar.edu.ungs.pps2.jgentest.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import spoon.reflect.declaration.CtMethod;

public class MethodToSelect
{
	private final StringProperty	_methodNameSignature	= new SimpleStringProperty();
	private final CtMethod<?>		_method;
	private final BooleanProperty	_isSelected				= new SimpleBooleanProperty();

	// Por defecto no está seleccionado un método
	public MethodToSelect(CtMethod<?> method)
	{
		// this._methodName.set(method);
		this._method = method;
		setMethodNameSignature(_method.getSignature());
		this._isSelected.set(false);
	}

	public BooleanProperty isSelectedProperty()
	{
		return _isSelected;
	}

	public boolean isSelected()
	{
		return _isSelected.get();
	}

	public StringProperty getMethodNameProperty()
	{
		return _methodNameSignature;
	}

	public String getMethodNameSignature()
	{
		return _methodNameSignature.get();
	}

	public void setMethodNameSignature(String name)
	{
		this._methodNameSignature.set(name);
	}

	public void setSelected(boolean isSelected)
	{
		this._isSelected.set(isSelected);
	}

	public CtMethod<?> getCtMethod()
	{
		return this._method;
	}

	@Override
	public String toString()
	{
		return getMethodNameSignature().toString();
	}

}
