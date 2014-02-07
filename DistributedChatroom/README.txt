Distributed RMI Chat System Version 1.0 11/7/2013

This project implements a distributed chatroom program using Java Remote Method Invocation (RMI).  The project
allows for users to create chatrooms of a specific topic, join chatrooms, and participate in chat with other clients
that have joined the chatroom.  We have also implemented a GUI for the application.

@Author(s): Nathaniel Wendt, Raga Srinivasan

--------------------------------------------------------------------------------------------------
SETUP INSTRUCTIONS
--------------------------------------------------------------------------------------------------

1) Open the Setup.Init.java file and change the REGISTRY_LOOKUP_STRING:

	For connecting clients running on the same machine:
	rmi://localhost/TheRegistry

	For connecting clients across machines:
	rmi://<IP_ADDRESS_OF_HOST_RUNNING_RMI_REGISTRY>/TheRegistry


--------------------------------------------------------------------------------------------------
RUNNING INSTRUCTIONS
--------------------------------------------------------------------------------------------------
1) Compile/Build the program in your favorite IDE or from the command line
2) Run the following main programs in this order:
	TheRegistryImpl
	*ChatRoomProviderGUI
	*ChatClientGUI
	
* After TheRegistryImpl is created, any order and combination of ChatClientGUI and ChatRoomProviderGUI
  can be run.
