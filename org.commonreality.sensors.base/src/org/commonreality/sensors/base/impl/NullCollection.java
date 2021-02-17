package org.commonreality.sensors.base.impl;

import java.util.Collection;
import java.util.Iterator;

public class NullCollection<E> implements Collection<E>
{

  public NullCollection()
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean add(E arg0)
  {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> arg0)
  {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public void clear()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean contains(Object arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEmpty()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterator<E> iterator()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean remove(Object arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> arg0)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int size()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object[] toArray()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
