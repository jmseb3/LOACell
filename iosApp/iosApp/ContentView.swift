import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()
    
    let bottomHeight : CGFloat = 80
    var body: some View {
        ZStack {
            VStack {
                if(viewModel.user == nil) {
                    LoginView()
                } else {
                    VStack {
                        DialogHost(
                            dialogStatus: viewModel.dialogStatus,
                            dialogAction: viewModel.getDialogAction()
                        ) {
                            SnackBarHost(bottomSpace: bottomHeight + 20) {
                                TopAppBar()
                                Divider()
                                MainContent()
                                BottomAppBar(height: bottomHeight)
                            }
                        }
                    }
                }
            }
            if viewModel.showSplash {
                VStack(alignment : .center) {
                    Image(resource: \.logo)
                }
                .frame(maxWidth: .infinity,maxHeight: .infinity)
                .background(.white)
            }
        }
        .environmentObject(viewModel)
        .environmentObject(viewModel.sc)
        .onOpenURL { url in
            print("Received URL: \(url)")
            guard let uniqueId = url.queryParameters["uniqueId"] else {return}
            print(uniqueId)
            viewModel.checkByScheme(roomId: uniqueId)
        }
    }
}
