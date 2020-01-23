//
//  ViewController.swift
//  Danny Interactive
//
//  Created by Mercantec Elev on 21/01/2020.
//  Copyright © 2020 Mercantec Elev. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var leading: NSLayoutConstraint!
    
    @IBOutlet weak var trailing: NSLayoutConstraint!
    
    var menuShown = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    @IBAction func menuTapped(_ sender: Any) {
        
        if(menuShown == false)
        {
            leading.constant = 150
            trailing.constant = 150
            menuShown = true
        }
        else
        {
            leading.constant = 0
            trailing.constant = 0
            menuShown = false
        }
        
    }
    

}

