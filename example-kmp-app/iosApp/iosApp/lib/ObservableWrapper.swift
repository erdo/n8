import SwiftUI
import Combine
import shared

class ObservableState<S>: ObservableObject {
    
    @Published var state: S

    private let model: Observable
    private var observer: Observer?

    init(model: Observable, _ state: @escaping () -> S) {
        
        self.model = model
        self.state = state()
        
        observer = ObserverWrapper(){
            self.state = state()
        }
        
        model.addObserver(observer: observer.unsafelyUnwrapped)
    }

    deinit {
        model.removeObserver(observer: observer.unsafelyUnwrapped)
    }
}

class ObserverWrapper: Observer {
    private let onChange: () -> Void

    init(_ onChange: @escaping () -> Void) {
        self.onChange = onChange
    }

    func somethingChanged() {
        onChange()
    }
}

extension Observable {
    func toObservableState<S>(state: @escaping () -> S) -> ObservableState<S> {
        return ObservableState<S>(model: self, state)
    }
}

extension Observable {
    func toStateObject<S>(_ state: @escaping () -> S) -> StateObject<ObservableState<S>> {
        return StateObject(wrappedValue: self.toObservableState(state: state))
    }
}
