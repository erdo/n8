import Foundation
import shared

/*
 simple manual DI for the sample, but you do you. get dependencies from anywhere like this counterModel = OG[CounterModel.self]
 */
final class OG {

    private static let instance = OG()
    private var dependencies: [String: Any] = [:]
    
    private var initialized = false

    private init() {}

    static func create() {
        
        #if DEBUG
        // commenting out the logging will make things run noticeably faster
//        Fore.Companion().setDelegate(
//            delegate:WarningsAndErrorsDelegate(
//                tagPrefix: "foo_"
//            )
//        )
        Fore.Companion().setDelegate(
            delegate:DebugDelegateDefault(tagPrefix: "foo_")
        )
        #endif
        
        // because of the complicated generics, it's easier
        // to create the navigation model in kotlin land
        NavKt.createNavigation(application: nil)
        
        // other observable models that are part of your object graph
        
        
        // register dependencies for later
        instance.register(NavKt.getNavigation(), as: NavigationModel<Location, TabHostId>.self)
    }
    
    static func initialize() {
        if (!instance.initialized) {
            instance.initialized = true

            // run any necessary initialization code once object graph has been created here

        }
    }

    private func register<T>(_ dependency: T, as type: T.Type) {
        dependencies[String(describing: type)] = dependency
    }
    
    static subscript<T>(type: T.Type) -> T {
        guard let dependency = instance.dependencies[String(describing: type)] as? T else {
            fatalError("No dependency registered for type \(type)")
        }
        return dependency
    }

    func putMock<T>(_ instance: T, as type: T.Type) {
        OG.instance.register(instance, as: type)
    }
}
