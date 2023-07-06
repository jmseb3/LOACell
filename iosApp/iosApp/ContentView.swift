import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: LoaCellViewModel
    
    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView(viewModel: viewModel)
            } else {
                Button("out") {
                    viewModel.signOut()
                }
            }
        }
    }
}
