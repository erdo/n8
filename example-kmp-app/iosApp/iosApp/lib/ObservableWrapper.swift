import SwiftUI
import Combine
import shared

class ForeObservableObject<S>: ObservableObject {
    
    @Published var state: S

    let foreModel: Observable
    private var observer: Observer?

    init(foreModel: Observable, _ state: @escaping () -> S) {
        
        self.foreModel = foreModel
        self.state = state()
        
        observer = ObserverWrapper(){
            self.state = state()
        }
        
        self.foreModel.addObserver(observer: observer.unsafelyUnwrapped)
    }

    deinit {
        self.foreModel.removeObserver(observer: observer.unsafelyUnwrapped)
    }
}


class ForeObservableState<S>: ObservableObject {
    
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
    func toObservableState<S>(state: @escaping () -> S) -> ForeObservableState<S> {
        return ForeObservableState<S>(model: self, state)
    }
}

extension Observable {
    func toStateObject<S>(_ state: @escaping () -> S) -> StateObject<ForeObservableState<S>> {
        return StateObject(wrappedValue: self.toObservableState(state: state))
    }
}
