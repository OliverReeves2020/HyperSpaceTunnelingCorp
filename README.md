
# Brief
**Hyperspace Tunneling Corp** manages a system-to-system web of hyperspace accelerators that spans the United Terran Systems. They charge a fee to their users in order to use their network but they want to expand their business.

Recently, they've expanded into transporting people through their network using light transport space-ships that can take up to 5 people to the accelerator and then use their hyperspace-enabled ships to travel to the destination accelerator.

We've been asked to develop a system to help calculate the costs of this journey for their clients.

A journey is defined as:

1. Journey to the accelerator:
    * **Personal Transport**: £0.30/[AU](https://en.wikipedia.org/wiki/Astronomical_unit) (standard fuel cost) plus £5 per day for ship storage at the accelerator - (fits up to 4 people)
    * **HTC Transport**: £0.45/[AU](https://en.wikipedia.org/wiki/Astronomical_unit) - (fits up to 5 people)

2. An outbound and an inbound hyperspace journey:
    * **Spaceflight**: £0.10/passenger/hyperplane-unit

HTC keeps a table with its accelerators and their connections:

| Accelerator ID | Accelerator Name | Connections and Hyperplane units of distance (HU)                          |
| -------------- | ---------------- | -------------------------------------------------------------------------- |
| SOL            | Sol              | RAN: 100HU<br/>PRX: 90HU<br/>SIR: 100HU<br/>ARC: 200HU<br/>ALD: 250HU<br/> |
| PRX            | Proxima          | SOL: 90HU<br/>SIR: 100HU<br/>ALT: 150HU<br/>                               |
| SIR            | Sirius           | SOL: 80HU<br/>PRX: 10HU<br/>CAS: 200HU<br/>                                |
| CAS            | Castor           | SIR: 200HU<br/>PRO: 120HU<br/>                                             |
| PRO            | Procyon          | CAS: 80HU<br/>                                                             |
| DEN            | Denebula         | PRO: 5HU<br/>ARC: 2HU<br/>FOM: 8HU<br/>RAN: 100HU<br/>ALD: 3HU<br/>        |
| RAN            | Ran              | SOL: 100HU<br/>                                                            |
| ARC            | Arcturus         | SOL: 500HU<br/>DEN: 120HU<br/>                                             |
| FOM            | Fomalhaut        | PRX: 10HU<br/>DEN: 20HU<br/>ALS: 9HU<br/>                                  |
| ALT            | Altair           | FOM: 140HU<br/>VEG: 220HU<br/>                                             |
| VEG            | Vega             | ARC: 220HU<br/>ALD: 580HU<br/>                                             |
| ALD            | Aldermain        | SOL: 200HU<br/>ALS: 160HU<br/>VEG: 320HU<br/>                              |
| ALS            | Alshain          | ALT: 1HU<br/>ALD: 1HU<br/>                                                 |

```mermaid
graph TD
    SOL[Sol]
    PRX[Proxima]
    SIR[Sirius]
    CAS[Castor]
    PRO[Procyon]
    DEN[Denebula]
    RAN[Ran]
    ARC[Arcturus]
    FOM[Fomalhaut]
    ALT[Altair]
    VEG[Vega]
    ALD[Aldermain]
    ALS[Alshain]

    SOL --100--> RAN
    SOL --90--> PRX
    SOL --100--> SIR
    SOL --200--> ARC
    SOL --250--> ALD
    PRX --90--> SOL
    PRX --100--> SIR
    PRX --150--> ALT
    SIR --80--> SOL
    SIR --10--> PRX
    SIR --200--> CAS
    CAS --200--> SIR
    CAS --80--> PRO
    PRO --80--> CAS
    DEN --5--> PRO
    DEN --2--> ARC
    DEN --8--> FOM
    DEN --100--> RAN
    DEN --3--> ALD
    RAN --100--> SOL
    ARC --500--> SOL
    ARC --120--> DEN
    FOM --10--> PRX
    FOM --20--> DEN
    FOM --9--> ALS
    ALT --140--> FOM
    ALT --220--> VEG
    VEG --220--> ARC
    VEG --580--> ALD
    ALD --200--> SOL
    ALD --160--> ALS
    ALD --320--> VEG
    ALS --1--> ALT
    ALS --1--> ALD

```

Accelerators are typically one-way, so while the route `A->B` can exist, it doesn't necessarily mean that `B->A` exists. Additionally, the hyperplane distance varies depending on which way you travel - Our best theories say the hyperplane not only does not match real-space but also has a preferred direction.


## The task:
Write a program that suggests the cheapest quote for a customer's journeys.

This program should expose its logic via an API so that it can be consumed by other services or a browser client.

The API should expose, at least, the following endpoints:
* `GET`: `/transport/{distance}?passengers={number}&parking={days}` - returns the cheapest vehicle to use (and the cost of the journey) for the given `distance` (in AUs), `number` or passengers and `days` of parking
  * Accelerators typically sit above the star, so if you're on Earth and want to travel to the Sol accelerator, the distance would be ~1AU.
* `GET`: `/accelerators` - returns a list of accelerators with their information
* `GET`: `/accelerators/{acceleratorID}` - returns the details of a single accelerator
* `GET`: `/accelerators/{acceleratorID}/to/{targetAcceleratorID}` - returns the cheapest route from `acceleratorID` to `targetAcceleratorID`

These endpoints should be public.

### Expectations
* A git repository with the solution
    * Application code
        * Use any programming language you like (unless stated otherwise)
    * API documentation - e.g. Swagger, Postman collection
    * Any tests
    * Any CI/CD configuration - e.g. github actions
    * Any infrastructure-like config - e.g. Dockerfile(s)
    * Any supporting scripts to generate, package, run, etc...
* A dockerhub image with the solution and instructions on how to run it (and/or a docker-compose file)
    * Optionally, feel free to host this for us somewhere as well.

# notes
* for the cheapest **Spaceflight** we can use a traversal [algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
* host the rest on tomcat wrapped in docer image-> could deploy on azure

