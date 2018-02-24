package server;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Chatroom implements Comparable<Chatroom>{

	private Set<User> users;
	private String name;
	
	public Chatroom(String name)
	{
		users = new ConcurrentSkipListSet<>();
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean hasUser(User user)
	{
		return users.contains(user);
	}
	
	public boolean addUser(User user)
	{
		if (user != null)
		{
			return users.add(user);
		}
		return false;
	}
	
	public boolean removeUser(User user)
	{
		if (user != null)
		{
			return users.remove(user);
		}
		return false;
	}
	
	public Set<User> getAllUsers()
	{
		return Collections.unmodifiableSet(users);
	}

	@Override
	public int compareTo(Chatroom o) {
		return name.compareTo(o.name);
	}
	
}
