import XCTest
import shared
@testable import iosApp

class N8ControllerTests: XCTestCase {
    
    var controller: N8PathController!
    
    override func setUp() {
        super.setUp()
        controller = N8PathController(1)
    }
    
    override func tearDown() {
        controller = nil
        super.tearDown()
    }
    
    // MARK: - tests for empty path
    
    func testNavigateToEmptyPathWhenPathEmpty() {
        
        // arrange
        controller = N8PathController(0)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForNoItems()
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 0)
    }
    
    func testNavigateToEmptyPathWhenPathCount1() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForNoItems()
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 0)
    }
    
    func testNavigateToEmptyPathWhenPathCount2() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForNoItems()
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 0)
    }
    
    func testNavigateToEmptyPathWhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForNoItems()
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 0)
    }
    
    
    
    // MARK: - tests for path with single location
    
    func testNavigateToPathCount1WhenPathEmpty() {
        
        // arrange
        controller = N8PathController(0)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForOneItem()
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 1)
    }
    
    func testNavigateToPathCount1WhenPathCount1() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForOneItem()
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 1)
    }
    
    func testNavigateToPathCount1WhenPathCount2() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForOneItem()
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 1)
    }
    
    func testNavigateToPathCount1WhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForOneItem()
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 1)
    }
    
    
    // MARK: - tests for path with two locations
    
    func testNavigateToPathCount2WhenPathEmpty() {
        
        // arrange
        controller = N8PathController(0)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForTwoItems()
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 2)
    }
    
    func testNavigateToPathCount2WhenPathCount1() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForTwoItems()
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 2)
    }
    
    func testNavigateToPathCount2WhenPathCount2() {
        
        // arrange
        controller = N8PathController(0)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForTwoItems()
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 2)
    }
    
    func testNavigateToPathCount2WhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForTwoItems()
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 2)
    }
    
    
    
    // MARK: - tests for path with three locations
    
    func testNavigateToPathCount3PlusWithPushWhenPathEmpty() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPopWhenPathEmpty() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithSwitchWhenPathEmpty() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Switch())
        
        // assert
        XCTAssertEqual(initialCount, 0)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPushWhenPathCount1() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPopWhenPathCount1() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithSwitchWhenPathCount1() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForOneItem()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Switch())
        
        // assert
        XCTAssertEqual(initialCount, 1)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPushWhenPathCount2() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPopWhenPathCount2() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithSwitchWhenPathCount2() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForTwoItems()
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Switch())
        
        // assert
        XCTAssertEqual(initialCount, 2)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    func testNavigateToPathCount3PlusWithPushWhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize + 1)
    }
    
    func testNavigateToPathCount3PlusWithPopWhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize - 1)
    }
    
    func testNavigateToPathCount3PlusWithSwitchWhenPathCount3Plus() {
        
        // arrange
        let backBufferSize: Int = 50
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Switch())
        
        // assert
        XCTAssertEqual(initialCount, 3 + backBufferSize)
        XCTAssertEqual(controller.path.count, 3 + backBufferSize)
    }
    
    
    // MARK: - Edge Cases and Error Handling
    
    func testNavigateBackBeyondBuffer() {
        
        // arrange
        let backBufferSize: Int = 3
        controller = N8PathController(backBufferSize)
        controller.setPathForThreeOrMoreItems(with: OperationType.Push())
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        let initialCount = controller.path.count
        
        // act
        controller.setPathForThreeOrMoreItems(with: OperationType.Pop())
        
        // assert
        XCTAssertEqual(initialCount, 3)
        XCTAssertEqual(controller.path.count, 3)
    }
}
