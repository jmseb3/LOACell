import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
    
    var body: some View {
        if viewModel.characterList.isEmpty {
            Text(viewModel.text)
        } else {
            ForEach(viewModel.characterList, id:\.self) { item in
                itemView(item: item)
            }
        }
    }
}

struct itemView :View {
    let item : CharacterInfo
    var body: some View {
        HStack() {
            Text(item.characterName)
            Text(item.characterClassName)
            Text("\(item.characterLevel)")
        }
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var characterList = [CharacterInfo]()
        @Published var text : String = "Loading..."
        let lostarkApi = LostArkApi()
        init() {
            updateInfo()
        }

        func updateInfo() {
            lostarkApi.getCharacterInfo(characterName: "아이오에스티떡상가즈아") { itemList,error in
                DispatchQueue.main.async {
                    if let itemList = itemList {
                        prin
                    } else {
                        self.text = error?.localizedDescription ?? "error"
                    }
                }
            }
        }
    }
}
