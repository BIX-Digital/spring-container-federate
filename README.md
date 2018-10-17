# spring-container-federate

## Basic idea
Allow a spring based bean network to federate accross an openshift cluster in order to scale beans (services) adhoc thru pods.
This is a PoC implementation :)

## Architecture
Pretty simple, use of standard spring features, namely `BeanPostProcessor` and `FieldCallback`- to create "client" and 
"serverside" proxies - that are kicked into the initial context.

## Usage
See `com.bix_digital.platform.federate.DemoApplicationTests` for how the two new annotations
`@ContainerAwareAutoWired`and `@ContainerAwareService` are used.

## Open issues / status
1. Openshift integration (dynamically spinning up pods)