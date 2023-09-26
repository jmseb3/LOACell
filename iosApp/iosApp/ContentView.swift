import SwiftUI
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()
    
    
    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView(viewModel: viewModel)
            } else {
                VStack{
                    
                }
                Divider()
                ZStack {
                    VStack {
                        
                        MainContent(viewModel: viewModel)
                        Button("out") {
                            LoginHelper.instance.signOut()
                        }
                      
                    }
                    if(!viewModel.snackBarMessage.isEmpty) {
                        VStack {
                            Spacer()
                            HStack{
                                Text(viewModel.snackBarMessage)
                            }
                        }.onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 4.5) {
                                viewModel.snackBarMessage = ""
                            }
                        }
                    }
                }
                Divider()
                VStack {
                    
                }
            }
        }
    }
}
