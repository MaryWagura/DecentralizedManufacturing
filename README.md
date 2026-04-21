# Decentralized Manufacturing System

## Project Overview

The **Decentralized Manufacturing System** is a multi-agent system built with **JADE (Java Agent Development Framework)** that simulates an intelligent, decentralized manufacturing environment. The system demonstrates how autonomous agents can collaborate to fulfill manufacturing orders through dynamic negotiation and bidding mechanisms.

### Key Features

- **Agent-Based Architecture**: Uses autonomous agents to represent different manufacturing entities
- **Service Discovery**: Agents register their capabilities and discover available services
- **Contract Net Protocol**: Implements a sophisticated bidding system where machine agents propose solutions to order requests
- **Dynamic Negotiation**: Order agents evaluate bids and award jobs to the best (fastest) machine
- **Scalable Design**: Easy to add more machines or orders without modifying core logic

---

## System Components

### 1. **OrderAgent**
Represents a manufacturing order that needs to be fulfilled. Each OrderAgent:
- Specifies a required manufacturing operation (e.g., "welding", "painting")
- Searches the system for capable machines
- Broadcasts a Call for Proposal (CFP) to all available machines
- Collects bids from all machines
- Evaluates bids and awards the job to the machine with the shortest processing time
- Communicates accept/reject decisions to the bidding machines

### 2. **MachineAgent**
Represents a manufacturing machine with specific capabilities. Each MachineAgent:
- Registers its capability with the system (e.g., "drilling", "polishing")
- Listens for incoming job requests (CFP messages)
- Evaluates each request and generates a competitive bid (processing time)
- Receives notification of whether it won or lost the bid
- Is ready to process the next job

---

## Development Phases

### **Phase 1: Foundation & Basic Agent Setup**
**Objective**: Establish the core agent structure and lifecycle management

**What was implemented**:
- Created `MachineAgent` and `OrderAgent` classes extending JADE's `Agent` class
- Implemented `setup()` method to initialize agents with capabilities/requirements
- Implemented `takeDown()` method for graceful agent shutdown
- Established basic command-line argument passing to configure agents

**Key Learning**: Understanding the JADE agent lifecycle and how agents initialize with custom parameters.

---

### **Phase 2: Agent Registration with Directory Facilitator (DF)**
**Objective**: Enable agents to register their services in a centralized registry

**What was implemented**:
- Machines register their capabilities with the DF service (Yellow Pages)
- Used `DFService.register()` to publish machine services
- Implemented proper service description with type and name attributes
- Added exception handling for DF registration failures
- Implemented cleanup with `DFService.deregister()` during takeDown

**Key Learning**: How agents publish and manage service registrations in a distributed system.

---

### **Phase 3: Service Discovery**
**Objective**: Enable order agents to find machines with required capabilities

**What was implemented**:
- Created `FindMachineBehaviour` behavior to search the DF
- Implemented service template matching to find machines by operation type
- Used `DFService.search()` to query for available machines
- Handled cases where no suitable machines are found
- Collected references to all matching machines for the bidding phase

**Key Learning**: How to implement service discovery patterns in a decentralized system.

---

### **Phase 4: Basic Agent Communication**
**Objective**: Establish message-based communication between agents

**What was implemented**:
- Implemented `ProcessOrderBehaviour` to receive and respond to messages
- Used ACL (Agent Communication Language) message protocol
- Implemented basic message handling with switch statements
- Added different message types: CFP, PROPOSE, ACCEPT_PROPOSAL, REJECT_PROPOSAL
- Created cyclic behaviors to maintain continuous message listening

**Key Learning**: How agents communicate asynchronously using standardized protocols.

---

### **Phase 5: Contract Net Protocol & Bidding System**
**Objective**: Implement a sophisticated negotiation framework for job assignment

**What was implemented**:

**OrderAgent**:
- Broadcasts CFP (Call for Proposal) to all capable machines
- Implemented `NegotiationBehaviour` to collect and evaluate bids
- Tracks expected number of replies vs. received replies
- Evaluates all proposals to find the best (lowest) processing time
- Sends ACCEPT_PROPOSAL message to the winning machine
- Sends REJECT_PROPOSAL messages to all other machines
- Automatically terminates negotiation behavior when complete

**MachineAgent**:
- Responds to CFP with PROPOSE messages containing a random processing time (10-50 units)
- Receives and handles ACCEPT_PROPOSAL (winner) and REJECT_PROPOSAL (loser) messages
- Provides visual feedback during negotiation process

**Key Features**:
- Dynamic bidding with realistic timing variations
- Competitive automatic selection process
- Clear console output showing the entire negotiation workflow
- Scalable to handle multiple concurrent orders and machines

**Key Learning**: Implementing the Contract Net Protocol, a foundational design pattern for multi-agent negotiations.

---

## System Architecture

### Message Flow

```
OrderAgent                          MachineAgent(s)
    |                                    |
    |---- CFP (Call for Proposal) ----->|
    |                                    |
    |<---- PROPOSE (Bid 1) -------------|
    |<---- PROPOSE (Bid 2) -------------|
    |<---- PROPOSE (Bid 3) -------------|
    |
    | [Evaluate Bids - Choose Best]
    |
    |---- ACCEPT_PROPOSAL (Winner) ---->|
    |---- REJECT_PROPOSAL (Loser) ----->|
    |---- REJECT_PROPOSAL (Loser) ----->|
```

### JADE Components Used

- **DFService**: Directory Facilitator for service registration and discovery
- **CyclicBehaviour**: For continuous message listening
- **OneShotBehaviour**: For one-time actions like service discovery
- **ACLMessage**: For agent-to-agent communication
- **DFAgentDescription & ServiceDescription**: For service metadata

---

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- JADE framework (jade.jar)
- IDE of your choice (IntelliJ IDEA recommended)

### Running the System

1. **Start JADE Main Container**:
   - Run the main JADE container with agents
   - Ensure the RMA (Remote Management Agent) is active for monitoring

2. **Launch Agents**:
   - Create MachineAgent instances with capabilities: "manufacturing-op-1", "manufacturing-op-2", etc.
   - Create OrderAgent instances requesting specific operations
   - Agents will automatically discover and negotiate

3. **Monitor Execution**:
   - Console output shows the complete negotiation process
   - Agent messages display when CFPs, proposals, accepts, and rejects occur

---

## Example Output

```
Machine Agent machine1 is ready. Capability: drilling
Machine Agent machine2 is ready. Capability: drilling
Machine Agent machine3 is ready. Capability: drilling

Order Agent order1 is ready. Looking for: drilling

Success! Found 3 capable machine(s). Sending CFPs...

>>> machine1 received CFP from order1
>>> machine2 received CFP from order1
>>> machine3 received CFP from order1

<<< machine1 proposing time: 35
<<< machine2 proposing time: 22
<<< machine3 proposing time: 45

>>> order1 received bid from machine1: 35 minutes.
>>> order1 received bid from machine2: 22 minutes.
>>> order1 received bid from machine3: 45 minutes.

--- All bids received! Evaluating ---

<<< Awarding job to: machine2 (Time: 22)

WINNER! >>> machine2 got the job! Starting manufacturing...
LOSER   >>> machine1 lost the bid. Back to waiting.
LOSER   >>> machine3 lost the bid. Back to waiting.

--- Negotiation Complete ---
```

---

## Future Enhancements

- **Job Execution Simulation**: Implement actual task execution after winning the bid
- **Cost-Based Bidding**: Extend beyond time consideration to include cost factors
- **Reputation System**: Track machine performance for future negotiations
- **Multi-Phase Orders**: Handle complex orders requiring multiple sequential operations
- **Load Balancing**: Implement agent migration for better resource utilization
- **Fault Tolerance**: Handle agent failures and message timeouts gracefully
- **Performance Metrics**: Collect and analyze system efficiency data

---

## Technologies Used

- **JADE - Java Agent Development Framework**: Multi-agent system middleware
- **Java**: Core programming language
- **ACL - Agent Communication Language**: Standardized agent messaging protocol
- **FIPA Standards**: Following FIPA specifications for agent interactions

---

## Author Notes

This project demonstrates fundamental concepts in multi-agent systems, particularly:
- Agent autonomy and decentralized decision-making
- Protocol-based communication
- Service-oriented architecture
- Contract negotiation patterns
- Scalable system design without central control

Perfect for learning about intelligent distributed systems!

---

*Last Updated: April 2026*
